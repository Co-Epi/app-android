package org.coepi.android.cen

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CENReportDao {
    @get:Query("SELECT * FROM cenreport")
    val all: List<CENReport?>?

    @Query("SELECT * FROM cenreport WHERE CENReportID IN (:id)")
    fun loadAllById(id: String?): List<CENReport?>?

    @Query("SELECT * FROM cenreport WHERE :first <= timeStamp AND timeStamp <= :last LIMIT 1")
    fun findByRange(first: Long, last: Long): List<CENReport>?

    @Insert
    fun insert(cenReport: CENReport?)

    @Delete
    fun delete(contact: CENReport?)
}