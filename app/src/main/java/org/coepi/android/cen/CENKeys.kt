package org.coepi.android.cen

import androidx.room.Entity

// ExposureCheckResponse contains a set of CENkeys (base64 encoded), which is used to match against CENs observed by client
@Entity
class CENKeys(_CENkeys : List<String>) {
    var CENKeys: List<String>? = _CENkeys
}
