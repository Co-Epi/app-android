package org.coepi.android.ui.common

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface ActivityFinisher {
    val observable: Observable<Unit>
    fun finish()
}

class ActivityFinisherImpl : ActivityFinisher {
    override val observable: PublishSubject<Unit> = PublishSubject.create()

    override fun finish() {
        observable.onNext(Unit)
    }
}
