package org.coepi.android.tcn

interface TcnKeyDao {
    fun lastTcnKeys(limit : Int): List<TcnKey>
    fun insert(key: TcnKey)
}
