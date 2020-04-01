package org.coepi.android.ble

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.coepi.android.ble.covidwatch.utils.toBytes
import org.coepi.android.cen.Cen
import java.util.UUID
import java.util.concurrent.TimeUnit.SECONDS

class BleSimulator : BleManager {
    override val scanObservable: Observable<Cen> =
        Observable.interval(0, 5, SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { Cen(UUID.randomUUID().toBytes()) }

    override fun startAdvertiser(cen: Cen) {}
    override fun stopAdvertiser() {}
    override fun startService(cen: Cen) {}
    override fun stopService() {}
    override fun changeAdvertisedValue(cen: Cen) {}
}
