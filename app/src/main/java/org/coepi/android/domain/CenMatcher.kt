package org.coepi.android.domain

import org.coepi.android.cen.CenKey
import org.coepi.android.cen.RealmCenDao

interface CenMatcher {
    fun hasMatches(cens: List<ByteArray>, key: CenKey, maxDate: CoEpiDate): Boolean
}

class CenMatcherImpl(
    private val cenLogic: CenLogic
) : CenMatcher {
    private val cenLifetimeInSeconds = 15 * 60   // every 15 mins a new CEN is generated

    override fun hasMatches(cens: List<ByteArray>, key: CenKey, maxDate: CoEpiDate): Boolean {
        val censSet: Set<ByteArray> = cens.toSet()

        val secondsInAWeek: Long = 604800

        val possibleCensCount = (secondsInAWeek / cenLifetimeInSeconds).toInt()
        for (i in 0..possibleCensCount) {
            val ts = maxDate.unixTime - cenLifetimeInSeconds * i
            val cen = cenLogic.generateCen(key, ts)
            if (censSet.contains(cen.bytes)) {
                return true
            }
        }
        return false
    }
}
