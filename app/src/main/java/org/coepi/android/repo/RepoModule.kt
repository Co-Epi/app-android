package org.coepi.android.repo

import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.SymptomInputsManager
import org.coepi.android.domain.symptomflow.SymptomInputsManagerImpl
import org.coepi.android.domain.symptomflow.SymptomRouter
import org.coepi.android.domain.symptomflow.SymptomRouterImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repoModule = module {
    single { RealmProvider(androidApplication()) }
    single<SymptomRepo> { SymptomRepoImpl(get()) }
    single<AlertsRepo> { AlertRepoImpl(get(), get()) }
    single<SymptomInputsManager> { SymptomInputsManagerImpl() }
    single { SymptomFlowManager(get(), get(), get(), get(), get()) }
    single<SymptomRouter> { SymptomRouterImpl() }
}
