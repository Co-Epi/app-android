package org.coepi.android.ui.cen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import org.coepi.android.ble.BleManager
import org.coepi.android.cen.CenRepo
import org.coepi.android.cen.RealmCen
import org.coepi.android.cen.RealmCenReport
import org.coepi.android.extensions.toLiveData
import org.coepi.android.ui.navigation.RootNavigation

class CENViewModel(
    repo: CenRepo,
    private val rootNav: RootNavigation,
    private val bleManager: BleManager
) : ViewModel() {

    val curcen = android.util.Base64.encode(repo.CEN.value,0);
    // CEN being broadcast by this device
    val myCurrentCEN = repo.CEN
        .map { "CEN: $it" }
        .toLiveData()

    // recently observed CENs
    val neighborCENs: LiveData<List<String>> = bleManager.scanObservable
        .scan(emptyList<String>()) { acc, element -> acc + element }
        .toLiveData()

    // TODO
    val cenReports: MutableLiveData<List<RealmCenReport>> by lazy {
        MutableLiveData<List<RealmCenReport>>()
    }

    private fun update() {
        // contacts.value = contactDao.findByRange(0, 99999999999)
        // symptoms.value = symptomsDao.findByRange(0, 99999999999)
    }
}

