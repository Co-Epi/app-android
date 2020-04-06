package org.coepi.android.ui.cen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import org.coepi.android.ble.BleManager
import org.coepi.android.cen.Cen
import org.coepi.android.cen.MyCenProvider
import org.coepi.android.cen.RealmCenReport
import org.coepi.android.cen.ReceivedCen
import org.coepi.android.extensions.coEpiTimestamp
import org.coepi.android.extensions.toLiveData
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.ui.navigation.RootNavigation
import java.util.Date

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
    val neighborCENs: LiveData<List<String>> = bleManager.scanObservable
        .scan(emptyList<Cen>()) { acc, element -> acc + element }
        .map { cens ->
            cens.map { it.toString() }
        }
        .toLiveData()

    // TODO
    val cenReports: MutableLiveData<List<RealmCenReport>> by lazy {
        MutableLiveData<List<RealmCenReport>>()
    }

    private fun String.hexToByteArray(): ByteArray {
        val carr = toCharArray()
        val size = carr.size
        val res = ByteArray(size / 2)
        var i = 0
        while (i < size) {
            val hex2 = "" + carr[i] + carr[i + 1]
            val byte: Byte = hex2.toLong(radix = 16).toByte()
            res[i / 2] = byte
            i += 2
        }
        return res
    }

    fun insertPastedCEN(centoinsert: String = "") {
        val curcenbytes = centoinsert.hexToByteArray()
        val c = Cen(curcenbytes)
        coeEpiRepo.storeObservedCen(ReceivedCen(c, Date().coEpiTimestamp()))
    }

    private fun update() {
        // contacts.value = contactDao.findByRange(0, 99999999999)
        // symptoms.value = symptomsDao.findByRange(0, 99999999999)
    }
}
