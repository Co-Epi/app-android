package org.coepi.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.coepi.android.R.id.rootNavHostFragment
import org.coepi.android.R.layout.activity_main
import org.coepi.android.ble.BleManager
import org.coepi.android.ble.BlePreconditions
import org.coepi.android.ble.BlePreconditionsNotifier
import org.coepi.android.cen.CenManager
import org.coepi.android.cen.CenRepo
import org.coepi.android.system.log.log
import org.coepi.android.ui.navigation.Navigator
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.onboarding.OnboardingShower
import org.koin.android.ext.android.inject
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

class MainActivity : AppCompatActivity() {
    private val rootNav: RootNavigation by inject()
    private val onboardingShower: OnboardingShower by inject()
    private val blePreconditionsNotifier: BlePreconditionsNotifier by inject()
    private val cenManager: CenManager by inject()

    private val disposables = CompositeDisposable()

    private var blePreconditions: BlePreconditions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        observeRootNavigation()

        blePreconditions = BlePreconditions(this) {
            blePreconditionsNotifier.notifyBleEnabled()
            log.i("BlePreconditions met - BLE manager started")
        }
        blePreconditions?.onActivityCreated()

        AppCenter.start(application, "0bb1bf95-3b14-48a6-a769-db1ff1df0307", Analytics::class.java, Crashes::class.java)
        cenManager.start()
    }

    private fun observeRootNavigation() {
        val navigator = Navigator(findNavController(rootNavHostFragment))
        disposables += rootNav.navigationCommands.subscribe {
            navigator.navigate(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        blePreconditions?.onActivityResult(requestCode, resultCode, data)
    }
}
