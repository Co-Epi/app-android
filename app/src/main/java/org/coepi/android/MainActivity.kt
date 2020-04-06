package org.coepi.android

import android.content.Intent
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
import org.coepi.android.ble.BlePreconditions
import org.coepi.android.cen.CenManager
import org.coepi.android.ui.navigation.Navigator
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.onboarding.OnboardingShower
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val rootNav: RootNavigation by inject()
    private val onboardingShower: OnboardingShower by inject()
    private val cenManager: CenManager by inject()
    private val blePreconditions: BlePreconditions by inject()

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        observeRootNavigation()
        onboardingShower.showIfNeeded()

        blePreconditions.onActivityCreated(this, applicationContext)

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
