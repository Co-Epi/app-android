package org.coepi.android.cen

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.ble.BleManager
import org.coepi.android.ble.Uuids
import org.coepi.android.ble.covidwatch.ScannedData
import org.coepi.android.ble.covidwatch.utils.UUIDs.CONTACT_EVENT_IDENTIFIER_CHARACTERISTIC
import org.coepi.android.ble.covidwatch.utils.UUIDs.CONTACT_EVENT_SERVICE
import org.coepi.android.system.log.log
import java.util.UUID

class CenManager(private val bleManager: BleManager, private val cenRepo: CenRepo) {

    private val disposables = CompositeDisposable()

    init {
        observeCen()
        observeScanner()
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
                bleManager.startAdvertiser(
                    CONTACT_EVENT_SERVICE,
                    CONTACT_EVENT_IDENTIFIER_CHARACTERISTIC
                )
            }
        }
    }

    private fun observeScanner() {
        disposables += bleManager.scanObservable
            .subscribeBy(onNext = {
                handleScannedData(it)
            }, onError = {
                log.e("Error scanning: $it")
            })
    }

    private fun handleScannedData(data: ScannedData) {
        for (i in data.serviceUuids.indices) {
            val serviceUuid = data.serviceUuids[i]

            if (serviceUuid.isCoepi()) {
                val serviceData = data.serviceData
                // *************** The ServiceData IS WHERE WE TAKE THE ANDROID CEN that the Android peripheral is advertising and we record it in Contacts
                // TODO make service data return the actual service data
                log.i("Discovered CoEpi with ServiceData: $serviceUuid $serviceData")
                cenRepo.insertCEN(serviceData)

            } else {
                // TODO review this. Seems weird.
                val x = Uuids.service.toString()
                val serviceData = data.serviceData
                log.d("Discovered non-CoEpi Service UUID: $x $serviceData")
                cenRepo.insertCEN(serviceData)
            }
        }
    }

    private fun UUID.isCoepi() =
        Uuids.service.toString() == toString()
}
