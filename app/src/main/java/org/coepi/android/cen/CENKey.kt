package org.coepi.android.cen

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// CENKey represents a BLE pairing between 2 devices
@Entity(tableName = "cenkey")
data class CENKey (
    @PrimaryKey val timeStamp: Int,
    @ColumnInfo(name = "cenkey") val cenKey: String?
)
