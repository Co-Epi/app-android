package org.coepi.android.cen

import io.realm.RealmList
import io.realm.RealmObject

// ExposureCheckResponse contains a set of CENkeys (base64 encoded), which is used to match against CENs observed by client
open class RealmCenKeys(
    var keys : RealmList<String> = RealmList()
): RealmObject()
