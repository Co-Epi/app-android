package org.coepi.android

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.coepi.android.api.NativeApi

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class FFISanityTests {
    @Test
    fun testSendReceiveString() {
        val n = NativeApi()
        val gr = n.testSendReceiveString("getReports")
        assertEquals(
            "Hello getReports",
            gr
        )
    }

    @Test
    fun testbootstrap_core() {
        val n = NativeApi()
        val gr = n.bootstrapCore("getReports2")
        assertEquals(
            "Hello getReports2",
            gr
        )
    }

    @Test
    fun testpostReport() {
        val n = NativeApi()
        val gr = n.postReport("getReports2")
        assertEquals(
            "Hello getReports2",
            gr
        )
    }

    @Test
    fun testSendStruct() {

    }

    @Test
    fun testReceiveStruct() {

    }

    @Test
    fun testSendAndReceiveStruct() {

    }

    @Test
    fun testCallback() {

    }

    @Test
    fun testRegisterCallback() {

    }
}
