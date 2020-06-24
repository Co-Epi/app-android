package org.coepi.android

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.coepi.android.R.id.rootNavHostFragment
import org.coepi.android.R.layout.activity_main
import org.coepi.android.R.style.AppTheme
import org.coepi.android.ble.BlePreconditions
import org.coepi.android.tcn.BleInitializer
import org.coepi.android.system.intent.IntentForwarder
import org.coepi.android.ui.common.ActivityFinisher
import org.coepi.android.ui.common.UINotification
import org.coepi.android.ui.common.UINotifier
import org.coepi.android.ui.navigation.Navigator
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.onboarding.OnboardingShower
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val rootNav: RootNavigation by inject()
    private val onboardingShower: OnboardingShower by inject()
    private val bleInitializer: BleInitializer by inject()
    private val blePreconditions: BlePreconditions by inject()
    private val nonReferencedDependenciesActivator: NotReferencedDependenciesActivator by inject()
    private val intentForwarder: IntentForwarder by inject()
    private val uiNotifier: UINotifier by inject()
    private val activityFinisher: ActivityFinisher by inject()

    private val disposables = CompositeDisposable()

    init {
        nonReferencedDependenciesActivator.activate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        observeRootNavigation()
        observeUINotifier()
        observeActivityFinisher()

        onboardingShower.showIfNeeded()

        intentForwarder.onActivityCreated(intent)

        blePreconditions.onActivityCreated(this)

        AppCenter.start(application, "0bb1bf95-3b14-48a6-a769-db1ff1df0307", Analytics::class.java, Crashes::class.java)
        bleInitializer.start()
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
        blePreconditions.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        blePreconditions.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[1] == PERMISSION_DENIED && shouldShowRequestPermissionRationale(permissions[1])) {
            AlertDialog.Builder(this)
                .setTitle(R.string.bluetooth_info_title)
                .setMessage(R.string.bluetooth_info_message)
                .setPositiveButton(R.string.ok, DialogInterface.OnClickListener
                { dialog, _ -> ActivityCompat.requestPermissions(this, permissions, requestCode)
                    //dialog.dismiss()
                })
                .setNegativeButton(R.string.dont_allow, DialogInterface.OnClickListener
                { dialog, _ ->
                    dialog.dismiss()
                })
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    object RequestCodes {
        const val onboardingPermissions = 1
        const val enableBluetooth = 2
    }
}
