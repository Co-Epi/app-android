package org.coepi.android.repo

import org.koin.dsl.module

val repoModule = module {
    single { ExposureRepo(get()) }
}
