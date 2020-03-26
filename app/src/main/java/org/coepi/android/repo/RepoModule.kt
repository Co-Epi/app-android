package org.coepi.android.repo

import org.coepi.android.repo.realm.ContactRepo
import org.coepi.android.repo.realm.RealmContactRepo
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repoModule = module {
    single { RealmProvider(androidApplication()) }
    single { ExposureRepo(get()) }
    single<ContactRepo> { RealmContactRepo(get()) }
    single<SymptomRepo> { SymptomRepoImpl() }
}
