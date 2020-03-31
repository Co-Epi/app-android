package org.coepi.android.ble

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.UUID
import java.util.concurrent.TimeUnit.SECONDS

class BleSimulator : BleManager {
    override val scanObservable: Observable<String> =
        Observable.interval(0, 5, SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { UUID.randomUUID().toString() } // TODO CEN

    override fun startAdvertiser(value: String) {}
    override fun stopAdvertiser() {}
    override fun startService(value: String) {}
    override fun stopService() {}
    override fun changeAdvertisedValue(value: String) {}
}
