package org.tcncoalition.tcnclient.bluetooth

import androidx.annotation.WorkerThread

interface TcnBluetoothServiceCallback {

    /** Callback whenever the service needs a new TCN for sharing. */
    @WorkerThread
    fun generateTcn(): ByteArray

    /** Callback whenever the service finds a TCN. */
    fun onTcnFound(tcn: ByteArray, estimatedDistance: Double? = null)
}
