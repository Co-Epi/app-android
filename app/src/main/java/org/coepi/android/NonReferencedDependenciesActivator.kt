package org.coepi.android

import org.coepi.android.cross.ScannedCensHandler

class NonReferencedDependenciesActivator(
    scannedCensHandler: ScannedCensHandler
) {
    init {
        scannedCensHandler.toString()
    }

    fun activate() {}
}
