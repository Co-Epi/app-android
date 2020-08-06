package org.coepi.android.system

import android.net.Uri
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create

interface WebLaunchEventEmitter {
    val uri: PublishSubject<Uri>
    fun launch(uri: Uri)
}

class WebLaunchEventEmitterImpl : WebLaunchEventEmitter {
    override val uri: PublishSubject<Uri> = create()

    override fun launch(uri: Uri) {
        this.uri.onNext(uri)
    }
}
