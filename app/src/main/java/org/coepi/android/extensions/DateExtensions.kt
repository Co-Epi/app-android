package org.coepi.android.extensions

import org.coepi.android.domain.UnixTime
import java.util.Date

fun Date.toUnixTime() = UnixTime.fromDate(this)
