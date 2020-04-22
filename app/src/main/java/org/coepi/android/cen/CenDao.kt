package org.coepi.android.cen

import org.coepi.android.domain.UnixTime

interface CenDao {
    fun all(): List<ReceivedCen>
    fun matchCENs(start: UnixTime, end: UnixTime, cens: Array<String>): List<ReceivedCen>
    fun findCen(cen: Cen): ReceivedCen?
    fun insert(cen: ReceivedCen): Boolean
}
