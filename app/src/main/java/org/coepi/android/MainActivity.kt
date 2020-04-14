package org.coepi.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State.SUCCEEDED
import androidx.work.WorkManager
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.coepi.android.R.drawable
import org.coepi.android.R.id.rootNavHostFragment
import org.coepi.android.R.layout.activity_main
import org.coepi.android.R.plurals
import org.coepi.android.R.string
import org.coepi.android.ble.BlePreconditions
import org.coepi.android.cen.BleInitializer
import org.coepi.android.cross.CenReportsNotifier
import org.coepi.android.system.intent.IntentForwarder
import org.coepi.android.ui.navigation.Navigator
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.notifications.AppNotificationChannels
import org.coepi.android.ui.notifications.NotificationConfig
import org.coepi.android.ui.notifications.NotificationPriority.HIGH
import org.coepi.android.ui.notifications.NotificationsShower
import org.coepi.android.ui.onboarding.OnboardingShower
import org.coepi.android.worker.cenfetcher.ContactsFetchManager
import org.coepi.android.worker.cenfetcher.ContactsFetchWorker
import org.coepi.android.worker.cenfetcher.ContactsFetchWorker.Companion
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val rootNav: RootNavigation by inject()
    private val onboardingShower: OnboardingShower by inject()
    private val cenManager: BleInitializer by inject()
    private val blePreconditions: BlePreconditions by inject()
    private val nonReferencedDependenciesActivator: NonReferencedDependenciesActivator by inject()
    private val cenReportsNotifier: CenReportsNotifier by inject()
    private val intentForwarder: IntentForwarder by inject()

    private val disposables = CompositeDisposable()

    init {
        nonReferencedDependenciesActivator.activate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        observeRootNavigation()
        onboardingShower.showIfNeeded()

        intentForwarder.onActivityCreated(intent)

        cenReportsNotifier.attach(this)

        blePreconditions.onActivityCreated(this)

        AppCenter.start(application, "0bb1bf95-3b14-48a6-a769-db1ff1df0307", Analytics::class.java, Crashes::class.java)
        cenManager.start()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intentForwarder.onNewIntent(intent)
    }

    private fun observeRootNavigation() {
        val navigator = Navigator(findNavController(rootNavHostFragment))
        disposables += rootNav.navigationCommands.subscribe {
            navigator.navigate(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        blePreconditions.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        blePreconditions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    object RequestCodes {
        const val onboardingPermissions = 1
        const val enableBluetooth = 2
    }
}
