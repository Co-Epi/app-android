package org.coepi.android.tcn

import org.coepi.android.domain.UnixTime

data class ReceivedTcn(val tcn: Tcn, val timestamp: UnixTime)
