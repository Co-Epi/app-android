package org.coepi.android.ui.symptoms

import android.util.Base64
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

class SymptomsViewModel(private val symptomRepo: SymptomRepo, private val repo: CenRepo ) : ViewModel() {

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
        val keys: String = "will be set by doPostSymptom using last 3 keys generated"
        val report = Base64.encodeToString("COVID 19 from BART".toByteArray(), Base64.NO_WRAP)
        val id = repo.toHex( "REPORT1".toByteArray())
        val timestamp = 0;//this will be set by doPostSymptom
        val symptomReport = SymptomReport(id, report,keys, timestamp)

        repo.doPostSymptoms( symptomReport )
        log.i("Reported: {reportID:\""+symptomReport.reportID+
                "\", report:\""+symptomReport.report+
                "\", cenkeys: \""+ symptomReport.cenKeys+
                "\", reportTimeStamp:\""+symptomReport.reportTimeStamp+"\"}" )

    }


    fun onSubmit() {
        submitSymptoms()
        submitTrigger.onNext(Unit)
    }

    private fun Symptom.toViewData(isChecked: Boolean) =
        SymptomViewData(name, isChecked, this)
}
