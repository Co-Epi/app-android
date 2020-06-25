package org.coepi.android.extensions

import org.coepi.core.domain.model.UnixTime
import org.threeten.bp.Instant

fun Instant.toUnixTime() = UnixTime.fromInstant(this)
