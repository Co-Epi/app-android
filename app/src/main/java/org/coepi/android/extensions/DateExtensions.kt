package org.coepi.android.extensions

import org.coepi.core.domain.model.UnixTime
import java.util.Date

fun Date.toUnixTime() = UnixTime.fromDate(this)
