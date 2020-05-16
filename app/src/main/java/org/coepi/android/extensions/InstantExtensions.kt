package org.coepi.android.extensions

import org.coepi.android.domain.UnixTime
import org.threeten.bp.Instant

fun Instant.toUnixTime() = UnixTime.fromInstant(this)
