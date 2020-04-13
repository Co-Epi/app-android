package org.coepi.android.domain

import org.coepi.android.cen.CenKey
import org.coepi.android.cen.RealmCenDao
import org.coepi.android.cen.RealmReceivedCen
import org.coepi.android.domain.CoEpiDate.Companion.fromSeconds

interface CenMatcher {
    fun hasMatches(key: CenKey, maxDate: CoEpiDate): Boolean
    fun match(key: CenKey, maxDate: CoEpiDate): List<RealmReceivedCen>
}

class CenMatcherImpl(
    private val cenDao: RealmCenDao,
    private val cenLogic: CenLogic
) : CenMatcher {
    private val cenLifetimeInSeconds = 15 * 60   // every 15 mins a new CEN is generated

    override fun hasMatches(key: CenKey, maxDate: CoEpiDate): Boolean =
        match(key, maxDate).isNotEmpty()

    // matchCENKey uses a publicized key and finds matches with one database call per key
    //  Not efficient... It would be best if all observed CENs are loaded into memory
    override fun match(key: CenKey, maxDate: CoEpiDate): List<RealmReceivedCen> {
        val secondsInAWeek: Long = 604800

        // take the last 7 days of timestamps and generate all the possible CENs (e.g. 7 days) TODO: Parallelize this?

        val minTimestampSeconds = maxDate.unixTime - secondsInAWeek
        val possibleCensCount = (secondsInAWeek / cenLifetimeInSeconds).toInt()

        val possibleCENs = Array(possibleCensCount) { i ->
            val ts = maxDate.unixTime - cenLifetimeInSeconds * i
            val cen = cenLogic.generateCen(key, ts)
            cen.toHex()
        }

        // check if the possibleCENs are in the CEN Table
        return cenDao.matchCENs(fromSeconds(minTimestampSeconds), maxDate, possibleCENs)
    }
}
