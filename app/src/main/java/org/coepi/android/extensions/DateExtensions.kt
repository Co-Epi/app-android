package org.coepi.android.extensions

import java.util.Date

// Unix time (seconds)
fun Date.coEpiTimestamp() = time / 1000
