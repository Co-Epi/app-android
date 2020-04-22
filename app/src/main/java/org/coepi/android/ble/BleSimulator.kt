package org.coepi.android.ble

import io.reactivex.Observable
import io.reactivex.Observable.fromIterable
import org.coepi.android.cen.Cen
import org.coepi.android.cen.CenKey
import org.coepi.android.domain.CenLogic
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.system.log.log

class BleSimulator(cenLogic: CenLogic) : BleManager {

    // Keys to test locally
    private val keys: List<CenKey> = listOf(
        CenKey("F2BA29936ED6898157CD839FE50ACF40998533C3A4FECFD2B9FA252E0B10E14B", now()),
        CenKey("e7c63d828922422cbba7ffed3f858598d5c97ec34442ec875b74cffdf316edd6", now())
    )

    private val cens: List<Cen> = keys.map {
        cenLogic.generateCen(it, now().value)
    }

    // Emits all the cens at once and terminates
    override val observedCens: Observable<Cen> = fromIterable(cens)

    // Use this to emit periodically
//    private var currentCenIndex = 0
//    override val observedCens: Observable<Cen> =
//        Observable.interval(0, 5, SECONDS)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .flatMap {
//                if (cens.isEmpty()) {
//                    empty()
//                } else {
//                    just(cens[currentCenIndex % cens.size])
//                }
//            }

    init {
        log.i("Using Bluetooth simulator")
    }

    override fun startAdvertiser(cen: Cen) {}
    override fun stopAdvertiser() {}
    override fun startService(cen: Cen) {}
    override fun stopService() {}
    override fun changeAdvertisedValue(cen: Cen) {}
}
