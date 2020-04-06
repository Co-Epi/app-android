package org.coepi.android.cen

import io.reactivex.Observable
import io.reactivex.Observable.empty
import io.reactivex.Observable.just
import org.coepi.android.domain.CenLogic
import org.coepi.android.extensions.coEpiTimestamp
import org.coepi.android.system.log.LogTag.CEN_L
import org.coepi.android.system.log.log
import java.util.Date
import java.util.concurrent.TimeUnit.MINUTES

interface MyCenProvider {
    val cen: Observable<Cen>
}

class MyCenProviderImpl(
    private val cenLogic: CenLogic,
    private val cenKeyDao: RealmCenKeyDao
) : MyCenProvider {

    private var cenKeyTimestamp = 0L // TODO maybe observe directly a database query with the last key

    override val cen: Observable<Cen> = Observable.interval(0, 15, MINUTES)
        .flatMap {
            generateAndStoreNewCenKeyIfNeeded()
        }
        .map { key ->
            cenLogic.generateCen(key, key.timestamp)
        }

    private fun generateAndStoreNewCenKeyIfNeeded(): Observable<CenKey> {
        val curTimestamp = Date().coEpiTimestamp()
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
