package org.coepi.android.cen

import io.reactivex.Observable
import io.reactivex.Observable.empty
import io.reactivex.Observable.just
import org.coepi.android.domain.CenLogic
import org.coepi.android.domain.CoEpiDate
import org.coepi.android.domain.CoEpiDate.Companion.minDate
import org.coepi.android.domain.CoEpiDate.Companion.now
import org.coepi.android.system.log.LogTag.CEN_L
import org.coepi.android.system.log.log
import org.coepi.android.ui.debug.DebugBleObservable
import java.util.concurrent.TimeUnit.MINUTES

interface MyCenProvider {
    val cen: Observable<Cen>
}

class MyCenProviderImpl(
    private val cenLogic: CenLogic,
    private val cenKeyDao: RealmCenKeyDao,
    private val debugObservable: DebugBleObservable
) : MyCenProvider {

    private var cenKeyTimestamp: CoEpiDate = minDate() // TODO maybe observe directly a database query with the last key

    override val cen: Observable<Cen> = Observable.interval(0, 15, MINUTES)
        .flatMap { generateAndStoreNewCenKeyIfNeeded() }
        .doOnNext { debugObservable.setMyKey(it) }
        .map { key -> cenLogic.generateCen(key, key.date.unixTime) }
        .doOnNext { debugObservable.setMyCen(it) }
        .share()

    private fun generateAndStoreNewCenKeyIfNeeded(): Observable<CenKey> {
        val curTimestamp: CoEpiDate = now()
        return if (cenLogic.shouldGenerateNewCenKey(curTimestamp, cenKeyTimestamp)) {
            val key = cenLogic.generateCenKey(curTimestamp)
            log.i("Generated a new CEN key: $key", CEN_L)
            cenKeyDao.insert(key)
            just(key)
        } else {
            log.v("Passing CEN key generation...", CEN_L)
            empty()
        }
    }
}
