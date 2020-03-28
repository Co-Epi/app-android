package org.coepi.android.cen

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// CEN represents an observed Contact Event Number (CEN) from Peripherals
@Entity(tableName = "cen")
data class CEN(
    // raw CEN string (16 bytes with dashes == 20 char string)
    @PrimaryKey
    var CEN: String = "",

    // when you have seen it
    @ColumnInfo(name = "timeStamp")
    var timeStamp: Int = 0
)
