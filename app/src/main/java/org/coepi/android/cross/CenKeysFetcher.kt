package org.coepi.android.cross

import android.util.Base64
import io.reactivex.Observable
import io.reactivex.Observable.interval
import org.coepi.android.api.CENApi
import org.coepi.android.cen.CenKey
import org.coepi.android.extensions.coEpiTimestamp
import org.coepi.android.extensions.hexToByteArray
import java.util.Date
import java.util.concurrent.TimeUnit.MINUTES

interface CenKeysFetcher {
    val keys: Observable<List<CenKey>>
}

class CenKeysFetcherImpl(private val api: CENApi) : CenKeysFetcher {

    override val keys: Observable<List<CenKey>> = interval(0, 30, MINUTES)
        .flatMap {
            // TODO timestamp here
            api.cenkeysCheck(0).toObservable()
        }
        .map { strings ->
            strings.map {
                CenKey(it, Date().coEpiTimestamp()) }
        }
}
