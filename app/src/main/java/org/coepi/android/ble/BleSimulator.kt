package org.coepi.android.ble

import io.reactivex.Observable
import io.reactivex.Observable.fromIterable
import org.coepi.android.cen.Cen
import org.coepi.android.extensions.base64ToByteArray
import org.coepi.android.system.log.log
import org.tcncoalition.tcnclient.crypto.SignedReport

class BleSimulator : BleManager {

    // Reports used to derive fake observed TCNs from
    private val reports: List<String> = listOf(
//        "LbSUvv320gtY2qTZbumxno7KJ/BDWnHuHcUH0fNv144p+K1xbPt+YQuxFHzFfo71HoegSspNJLaAz93InuQHHQEAAQAACFJtVjJaWEk9iXj1FGy+r4cmNrS84AzHzx5wS0FZJXzXFFfvqwAogt6qjIe7+6CIJ8mrFrCen3nAVrQo3Bd1jsGe6UjybRUlAA=="
        "rSqWpM3ZQm7hfQ3q2x2llnFHiNhyRrUQPKEtJ33VKQcwT7Ly6e4KGaj5ZzjWt0m4c0v5n/VH5HO9UXbPXvsQTgEAQQAALFVtMVdNbHBZU1hOSlJYaDJZek5OWjJJeVdXZFpXRUozV2xoU2NHUkhWVDA9jn0pZAeME6ZBRHJOlfIikyfS0Pjg6l0txhhz6hz4exTxv8ryA3/Z26OebSRwzRfRgLdWBfohaOwOcSaynKqVCg=="
    )

    private val cens: List<Cen> = reports.mapNotNull { report ->
        val signedReport = report.base64ToByteArray()?.let { SignedReport.fromByteArray(it) }
        signedReport?.report?.temporaryContactNumbers?.let {
            if (it.hasNext()) { it.next() } else { null }
        }?.let { Cen(it.bytes) }
    }

    // Emits all the cens at once and terminates
    override val observedCens: Observable<Cen> = fromIterable(cens)

    init {
        log.i("Using Bluetooth simulator")
        log.i("Bluetooth cens: $cens")
    }

    override fun startService() {}
    override fun stopService() {}
}
