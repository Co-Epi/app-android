package org.coepi.android.ui.symptoms

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.cen.CENApi
import org.coepi.android.cen.CenRepo
import org.coepi.android.cen.RealmCenDao
import org.coepi.android.cen.RealmCenKeyDao
import org.coepi.android.cen.RealmCenReport
import org.coepi.android.cen.RealmCenReportDao
import org.coepi.android.cen.SymptomReport
import org.coepi.android.domain.model.Symptom
import org.coepi.android.extensions.toLiveData
import org.coepi.android.extensions.toObservable
import org.coepi.android.extensions.toUnitObservable
import org.coepi.android.extensions.toggle
import org.coepi.android.repo.RealmProvider
import org.coepi.android.repo.SymptomRepo
import org.coepi.android.system.log.log
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

class SymptomsViewModel(private val symptomRepo: SymptomRepo, private val realmprovider: RealmProvider) : ViewModel() {

    private val selectedSymptomIds: BehaviorSubject<Set<String>> =
        BehaviorSubject.createDefault(emptySet())

    private val checkedSymptomTrigger: PublishSubject<SymptomViewData> = create()
    private val submitTrigger: PublishSubject<Unit> = create()


    private val symptomsObservable: Observable<List<SymptomViewData>> = Observables
        .combineLatest(symptomRepo.symptoms().toObservable(), selectedSymptomIds)
        .map { (symptoms, selectedIds) ->
            symptoms.map { it.toViewData(isChecked = selectedIds.contains(it.id)) }
        }

    val symptoms: LiveData<List<SymptomViewData>> = symptomsObservable.toLiveData()

    private val selectedSymptoms = symptomsObservable
        .map { symptoms -> symptoms
            .filter { it.isChecked }
            .map { it.symptom }
        }

    private val disposables = CompositeDisposable()

    init {
        disposables += checkedSymptomTrigger
            .withLatestFrom(selectedSymptomIds)
            .subscribe { (selectedSymptom, selectedIds) ->
                selectedSymptomIds.onNext(selectedIds.toggle(selectedSymptom.symptom.id))
        }

        disposables += submitTrigger
            .withLatestFrom(selectedSymptoms)
            .switchMap { (_, selectedSymptoms) ->
                symptomRepo.submitSymptoms(selectedSymptoms).toUnitObservable()
            }
            .subscribe()
    }

    fun onChecked(symptom: SymptomViewData) {
        checkedSymptomTrigger.onNext(symptom)
    }
    fun submitSymptoms(){
        log.i("About to send sypmtom:");
        if( realmprovider!= null ){
            val keys: String = "will be put by doPostSymptom using last 3 keys generated"
            val report = "COVID 19 confirmed"
            val id = 1
            val symptomReport = SymptomReport(id, report,keys)
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://coepi.wolk.com:8080")
                .build()
            val api = retrofit.create(CENApi::class.java)

            val cenDao = RealmCenDao(realmprovider)
            val cenkeyDao =  RealmCenKeyDao(realmprovider)
            val cenReportDao= RealmCenReportDao(realmprovider)
            val repo = CenRepo(api, cenDao, cenkeyDao, cenReportDao)
            repo.doPostSymptoms( symptomReport )
            log.i("Reported: {reportID:\""+symptomReport.reportID+"\", report:\""+symptomReport.report+"\", cenkeys: \""+ symptomReport.cenKeys+"\"}" )
        }else{
            log.i("Could not get context in init")
        }

    }


    fun onSubmit() {
        submitSymptoms()
        submitTrigger.onNext(Unit)
    }

    private fun Symptom.toViewData(isChecked: Boolean) =
        SymptomViewData(name, isChecked, this)
}
