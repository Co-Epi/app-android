package org.coepi.android.cross

import io.reactivex.Observable
import io.reactivex.Observable.interval
import org.coepi.android.api.CENApi
import org.coepi.android.cen.CenKey
import org.coepi.android.extensions.coEpiTimestamp
import org.coepi.android.extensions.rx.mapSuccess
import org.coepi.android.extensions.rx.toOperationState
import org.coepi.android.system.log.Log
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationState
import java.util.Date
import java.util.concurrent.TimeUnit.MINUTES

interface CenKeysFetcher {
    val keys: Observable<OperationState<List<CenKey>>>
}

class CenKeysFetcherImpl(private val api: CENApi) : CenKeysFetcher {

    override val keys: Observable<OperationState<List<CenKey>>> = interval(0, 30, MINUTES)
        .flatMap {
            // TODO timestamp here
            api.cenkeysCheck(0).toObservable().materialize()
        }
        .toOperationState()
        .mapSuccess { strings ->
            strings.map {
                CenKey(it, Date().coEpiTimestamp())
            }
        }
}
