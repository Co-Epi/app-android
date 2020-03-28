package org.coepi.android.cen

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Symptoms represents a symptoms report from the end user OR for others that have been matched
@Entity(tableName = "cenreport")
data class CENReport (
    @PrimaryKey
    // CENReportID is a local internal attribute only
    @SerializedName("cenReportID") var CENReportID: Int = 0,

    // report is a JSON Object (could be ByteArray) representing a report keyed by 2-4 CENKeys
    // Different app designs will have different ideas about what will go inside this report, differentiated by reportMimeType and other metadata that can be added to this
    @ColumnInfo(name = "report")
    @SerializedName("report")  var report: String = "",

    // CENKeys is a comma-separated string (should be: List of String) of 128bit CEN Keys
    @ColumnInfo(name = "cenkeys")
    @SerializedName("cenKeys") var CENKeys: String? = "",

    // The MIME Type of the above data, enabling everyone to build apps with different format
    @ColumnInfo(name = "reportMimeType")
    @SerializedName("reportMimeType") var reportMimeType: String,

    @ColumnInfo(name = "timeStamp")
    @SerializedName("reportTimeStamp") var reportTimestamp: Int? = 0,

    // isuser is true when the reporter is this user
    @ColumnInfo(name = "isUser")
    @SerializedName("isUser")  var isUser: Boolean
)
