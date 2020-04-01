package org.coepi.android.repo

import io.reactivex.Single
import org.coepi.android.cen.CenReport
import java.util.Date

interface AlertsRepo {
    fun notifications(): Single<List<CenReport>>
}

class AlertRepoImpl: AlertsRepo {

    override fun notifications(): Single<List<CenReport>> = Single.just(listOf(
        CenReport(1, "Report text1", "keys", "mime type", Date(), true),
        CenReport(2, "Report text2", "keys", "mime type", Date(), true),
        CenReport(3, "Report text3", "keys", "mime type", Date(), true),
        CenReport(4, "Report text4", "keys", "mime type", Date(), true),
        CenReport(5, "Report text5", "keys", "mime type", Date(), true),
        CenReport(6, "Report text6", "keys", "mime type", Date(), true),
        CenReport(7, "Report text7", "keys", "mime type", Date(), true)
    ))
}
