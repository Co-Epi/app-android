package org.coepi.android.extensions

import org.coepi.android.system.log.log
import org.coepi.core.domain.common.Result
import org.coepi.core.domain.common.Result.Success
import org.coepi.core.domain.common.Result.Failure

fun <T, E> Result<T, E>.expect(): T =
    when (this) {
        is Success -> success
        is Failure -> {
            // Logging before of a crash can be useful with a persistent log
            log.e("Failure: $error")
            error(error.toString())
        }
    }
