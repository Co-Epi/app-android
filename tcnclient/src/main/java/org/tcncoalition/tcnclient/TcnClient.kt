package org.tcncoalition.tcnclient

object TcnClient {
    @Volatile
    var tcnManager: TcnManager? = null

    fun init(tcnManager: TcnManager) {
        synchronized(this) {
            this.tcnManager = tcnManager
        }
    }
}