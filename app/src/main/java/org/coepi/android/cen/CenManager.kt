package org.coepi.android.cen

import org.coepi.android.ble.BleManager
import org.coepi.android.ble.covidwatch.utils.UUIDs.CONTACT_EVENT_IDENTIFIER_CHARACTERISTIC
import org.coepi.android.ble.covidwatch.utils.UUIDs.CONTACT_EVENT_SERVICE
import org.coepi.android.system.log.log

class CenManager(private val bleManager: BleManager, private val cenRepo: CenRepo) {

    init {
        observeCen()
    }

    private fun observeCen() {
        cenRepo.CEN.observeForever { cen ->
            // ServiceData holds Android Contact Event Number (CEN) that the Android peripheral is advertising
            val cenString = cen.toString()
            log.i("BleAdvertiser - observeForever CEN: $cenString")

            // TODO is check really needed? If yes, either add flag to advertiser or expose state and use here
//            if (started) {
                bleManager.stopAdvertiser()
//            }

            if (cen != null) {
                bleManager.startAdvertiser(CONTACT_EVENT_SERVICE, CONTACT_EVENT_IDENTIFIER_CHARACTERISTIC)
            }
        }
    }
}
