package org.coepi.android.ui.cen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import org.coepi.android.cen.CenRepo
import org.coepi.android.cen.RealmCen
import org.coepi.android.cen.RealmCenReport
import org.coepi.android.ui.navigation.RootNavigation

class CENViewModel(repo: CenRepo,
                   private val rootNav: RootNavigation) : ViewModel() {

    // CEN being broadcast by this device
    val myCurrentCEN = Transformations.map(repo.CEN) { cen ->
        "CEN: $cen"
    }
    // recently observed CENs
    val neighborCENs: MutableLiveData<List<RealmCen>> by lazy {
        MutableLiveData<List<RealmCen>>()
    }
    val cenReports: MutableLiveData<List<RealmCenReport>> by lazy {
            MutableLiveData<List<RealmCenReport>>()
    }

    private fun update() {
        // contacts.value = contactDao.findByRange(0, 99999999999)
        // symptoms.value = symptomsDao.findByRange(0, 99999999999)
    }
}

