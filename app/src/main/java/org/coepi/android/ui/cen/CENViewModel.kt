package org.coepi.android.ui.cen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import org.coepi.android.cen.CENRepo
import org.coepi.android.cen.CEN
import org.coepi.android.cen.CENReport
import org.coepi.android.ui.navigation.RootNavigation

class CENViewModel(repo: CENRepo,
                   private val rootNav: RootNavigation) : ViewModel() {

    // CEN being broadcast by this device
    val myCurrentCEN = Transformations.map(repo.CEN) { cen ->
        "CEN: $cen"
    }
    // recently observed CENs
    val neighborCENs: MutableLiveData<List<CEN>> by lazy {
        MutableLiveData<List<CEN>>()
    }
    val cenReports: MutableLiveData<List<CENReport>> by lazy {
            MutableLiveData<List<CENReport>>()
    }

    private fun update() {
        // contacts.value = contactDao.findByRange(0, 99999999999)
        // symptoms.value = symptomsDao.findByRange(0, 99999999999)
    }
}

