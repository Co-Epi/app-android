package org.coepi.android.ui.debug.cen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.coepi.android.ble.BleManager
import org.coepi.android.cen.Cen
import org.coepi.android.cen.MyCenProvider
import org.coepi.android.cen.RealmCenReport
import org.coepi.android.cen.ReceivedCen
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.extensions.hexToByteArray
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.CoEpiRepo

class CENViewModel(
    myCenProvider: MyCenProvider,
    bleManager: BleManager,
    private val coeEpiRepo: CoEpiRepo
) : ViewModel() {

    //val curcen = Base64.encode(repo.CEN.value,0);
    val curcenhex = myCenProvider.cen.map { it.toHex() }.toLiveData()

    // CEN being broadcast by this device
    val myCurrentCEN = myCenProvider.cen
        .map { it.toString() }
        .toLiveData()

    // recently observed CENs
    val neighborCENs: LiveData<List<String>> = bleManager.observedCens
        .scan(emptyList<Cen>()) { acc, element -> acc + element }
        .map { cens ->
            cens.map { it.toString() }
        }
        .toLiveData()

    // TODO
    val cenReports: MutableLiveData<List<RealmCenReport>> by lazy {
        MutableLiveData<List<RealmCenReport>>()
    }



    fun insertPastedCEN(centoinsert: String = "") {
        val curcenbytes = centoinsert.hexToByteArray()
        val c = Cen(curcenbytes)
        coeEpiRepo.storeObservedCen(ReceivedCen(c, now()))
    }

    private fun update() {
        // contacts.value = contactDao.findByRange(0, 99999999999)
        // symptoms.value = symptomsDao.findByRange(0, 99999999999)
    }
}
