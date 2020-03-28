package org.coepi.android.cen

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CENDao {
    @get:Query("SELECT * FROM cen")
    val all: List<CEN?>?

    @Query("SELECT * FROM cen WHERE :first <= timeStamp AND timeStamp <= :last and CEN in (:CENs)")
    fun matchCENs(first: Int?, last: Int?, CENs : Array<String>): List<CEN>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contact: CEN?)

//    @Delete("DELETE FROM cen where :timeStamp > timeStamp")
//    fun cleanCENs(timeStamp : Int)
}