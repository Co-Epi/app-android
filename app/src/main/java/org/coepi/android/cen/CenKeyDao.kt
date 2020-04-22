package org.coepi.android.cen

interface CenKeyDao {
    fun lastCENKeys(limit : Int): List<CenKey>
    fun insert(key: CenKey)
}
