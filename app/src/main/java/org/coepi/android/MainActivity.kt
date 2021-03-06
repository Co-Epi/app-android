package org.coepi.android

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.coepi.android.R.id.rootNavHostFragment
import org.coepi.android.R.layout.activity_main
import org.coepi.android.R.style.AppTheme
import org.coepi.android.ble.BleInitializer
import org.coepi.android.ble.BlePreconditions
import org.coepi.android.system.AppCenterInitializer
import org.coepi.android.system.LocaleProvider
import org.coepi.android.system.WebLaunchEventEmitter
import org.coepi.android.system.WebLauncher
import org.coepi.android.system.intent.IntentForwarder
import org.coepi.android.system.log.log
import org.coepi.android.ui.common.ActivityFinisher
import org.coepi.android.ui.common.UINotification
import org.coepi.android.ui.common.UINotifier
import org.coepi.android.ui.navigation.Navigator
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.notifications.ReminderAlarmHandler
import org.coepi.android.ui.onboarding.OnboardingShower
import org.koin.android.ext.android.inject
import java.util.Calendar
import java.util.Calendar.DATE
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NUMBER_OF_DAYS_TO_SET_THE_REMINDER_ALARM_FOR = 14
        private const val ALARM_TRIGGER_HOURS = 18
        private const val ALARM_TRIGGER_MINUTES = 0
    }

    private val rootNav: RootNavigation by inject()
    private val onboardingShower: OnboardingShower by inject()
    private val bleInitializer: BleInitializer by inject()
    private val blePreconditions: BlePreconditions by inject()
    private val nonReferencedDependenciesActivator: NotReferencedDependenciesActivator by inject()
    private val intentForwarder: IntentForwarder by inject()
    private val uiNotifier: UINotifier by inject()
    private val activityFinisher: ActivityFinisher by inject()
    private val localeProvider: LocaleProvider by inject()
    private val appCenterInitializer: AppCenterInitializer by inject()

    private val webLaunchEventEmitter: WebLaunchEventEmitter by inject()
    private val webLauncher: WebLauncher by inject()

    private val disposables = CompositeDisposable()

    init {
        nonReferencedDependenciesActivator.activate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        setReminderAlarmsFor(NUMBER_OF_DAYS_TO_SET_THE_REMINDER_ALARM_FOR)
        observeRootNavigation()
        observeUINotifier()
        observeActivityFinisher()
        observeWebLauncher()

        onboardingShower.showIfNeeded()

        // This can be generalized with activity events listeners
        intentForwarder.onActivityCreated(intent)
        blePreconditions.onActivityCreated(this)
        appCenterInitializer.onActivityCreated()

        bleInitializer.start()
    }

    private fun setReminderAlarmsFor(days: Int) {
        val calendar = Calendar.getInstance().apply {
            set(HOUR_OF_DAY, ALARM_TRIGGER_HOURS)
            set(MINUTE, ALARM_TRIGGER_MINUTES)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        (1..days).forEach { _ ->
            calendar.add(Calendar.DATE, 1)
            val notificationIdentifier = calendar.getIdentifierFromDate()
            val pendingIntent = intentWithRequestCode(notificationIdentifier, this)
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }

    }

    override fun onResume() {
        super.onResume()
        localeProvider.update()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intentForwarder.onNewIntent(intent)
    }

    private fun observeWebLauncher() {
        disposables += webLaunchEventEmitter.uri.subscribe {
            webLauncher.show(this, it)
        }
    }

    private fun observeRootNavigation() {
        val navigator = Navigator(findNavController(rootNavHostFragment))
        disposables += rootNav.navigationCommands.subscribe {
            navigator.navigate(it)
        }
    }

    private fun observeUINotifier() {
        disposables += uiNotifier.notifications.subscribe {
            UINotification().show(it, this)
        }
    }

    private fun observeActivityFinisher() {
        disposables += activityFinisher.observable.subscribe {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        blePreconditions.onActivityResult(requestCode, resultCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        blePreconditions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        blePreconditions.onActivityDestroy(this)
        disposables.clear()
    }

    object RequestCodes {
        const val onboardingPermissions = 1
        const val enableBluetooth = 2
    }
}

fun Calendar.getIdentifierFromDate() = String.format("%04d%02d%02d", get(YEAR), get(MONTH), get(DATE)).toInt()

fun intentWithRequestCode(code: Int, context: Context) : PendingIntent{
    log.d("[Reminder] scheduling: $code")
    return Intent(context, ReminderAlarmHandler::class.java).let { intent ->
        PendingIntent.getBroadcast(context, code, intent, FLAG_UPDATE_CURRENT)
    }
}