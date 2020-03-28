package org.coepi.android.repo

import org.coepi.android.repo.realm.ContactRepo
import org.coepi.android.repo.realm.RealmContactRepo
import org.coepi.android.repo.realm.SymptomRepo
import org.coepi.android.repo.realm.RealmSymptomRepo
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repoModuleRealm = module {
    single { RealmProvider(androidApplication()) }
    single { ExposureRepo(get()) }
    single<ContactRepo> { RealmContactRepo(get()) }
    single { RealmSymptomRepo(get()) }
}
