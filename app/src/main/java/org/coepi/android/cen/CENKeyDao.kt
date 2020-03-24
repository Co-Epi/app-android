package org.coepi.android.cen

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CENKeyDao {
    @Query("SELECT * FROM cenkey order by timestamp DESC limit :lim")
    fun lastCENKeys(lim : Int): List<CENKey>?

    @Insert
    fun insert(k: CENKey?)

//    @Query("SELECT * FROM cenkey WHERE :first <= timeStamp AND timeStamp <= :last LIMIT 1")
//    fun findByRange(first: Int?, last: Int?): List<CENKey>?

//    @Delete
//    fun deleteBefore(timestamp : Int)
}