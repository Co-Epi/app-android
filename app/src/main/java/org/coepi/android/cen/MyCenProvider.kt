package org.coepi.android.cen

import org.coepi.android.domain.CenLogic
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.system.log.LogTag.CEN_L
import org.coepi.android.system.log.log
import org.coepi.android.ui.debug.DebugBleObservable

interface MyCenProvider {
    fun generateCen(): Cen
}

class MyCenProviderImpl(
    private val cenLogic: CenLogic,
    private val cenKeyDao: CenKeyDao,
    private val debugObservable: DebugBleObservable
) : MyCenProvider {

    override fun generateCen(): Cen {
        val key: CenKey = retrieveOrGenerateCenKey()
        debugObservable.setMyKey(key)
        val cen: Cen = cenLogic.generateCen(key, now().value)
        debugObservable.setMyCen(cen)
        return cen
    }

    private fun retrieveOrGenerateCenKey(): CenKey {
        val now: UnixTime = now()
        return cenKeyDao.lastCENKeys(1).firstOrNull()
            ?.takeIf { !cenLogic.shouldGenerateNewCenKey(now, it.timestamp) }
            ?: generateAndInsertCenKey(now)
    }

    private fun generateAndInsertCenKey(timestamp: UnixTime): CenKey {
        val key = cenLogic.generateCenKey(timestamp)
        log.i("Generated a new CEN key: $key", CEN_L)
        cenKeyDao.insert(key)
        return key
    }
}
