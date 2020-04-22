package org.coepi.android.cen

import org.coepi.android.domain.CoEpiDate

interface CenDao {
    fun all(): List<ReceivedCen>
    fun matchCENs(start: CoEpiDate, end: CoEpiDate, cens: Array<String>): List<ReceivedCen>
    fun findCen(cen: Cen): ReceivedCen?
    fun insert(cen: ReceivedCen): Boolean
}
