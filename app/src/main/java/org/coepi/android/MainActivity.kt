package org.coepi.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.coepi.android.R.id.rootNavHostFragment
import org.coepi.android.R.layout.activity_main
import org.coepi.android.ble.BleDiscoveryImpl
import org.coepi.android.ble.BlePeripheral
import org.coepi.android.ble.BlePreconditions
import org.coepi.android.cen.CENRepo
import org.coepi.android.system.log.log
import org.coepi.android.ui.navigation.Navigator
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.onboarding.OnboardingShower
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val rootNav: RootNavigation by inject()
    private val repo: CENRepo by inject()
    private val onboardingShower: OnboardingShower by inject()

    private var disposables = CompositeDisposable()

    lateinit var blePreconditions : BlePreconditions
    lateinit var blePeripheral : BlePeripheral
    lateinit var bleDiscovery :  BleDiscoveryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        observeRootNavigation()
        blePreconditions = BlePreconditions(this) {
            bleDiscovery.discover()
            log.i("BlePreconditions met - BLE discover process started")
        }
        bleDiscovery = BleDiscoveryImpl(this.applicationContext)
        blePeripheral = BlePeripheral(this.applicationContext, repo)
        blePreconditions.onActivityCreated()
        log.i("MainActivity - onCreate")
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
}
