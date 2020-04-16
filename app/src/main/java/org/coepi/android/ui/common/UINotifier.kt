package org.coepi.android.ui.common

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface UINotifier {
    val notifications: Observable<UINotificationData>

    fun notify(data: UINotificationData)
}

class UINotifierImpl: UINotifier {
    override val notifications: PublishSubject<UINotificationData> = PublishSubject.create()

    override fun notify(data: UINotificationData) {
        notifications.onNext(data)
    }
}
