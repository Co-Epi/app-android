package org.coepi.android.ble

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Activity
import android.content.Intent
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.system.log.log

interface BlePreconditionsNotifier {
    val bleEnabled: Observable<Unit>

    fun notifyBleEnabled()
}

class BlePreconditionsNotifierImpl: BlePreconditionsNotifier {

    override val bleEnabled: BehaviorSubject<Unit> = BehaviorSubject.create<Unit>()

    override fun notifyBleEnabled() {
        bleEnabled.onNext(Unit)
    }
}
