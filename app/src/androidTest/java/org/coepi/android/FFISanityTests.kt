package org.coepi.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runBlockingTest
import org.coepi.android.api.Callback
import org.coepi.android.api.FFINestedParameterStruct
import org.coepi.android.api.FFIParameterStruct
import org.coepi.android.api.NativeApi
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FFISanityTests {

    @Test
    fun testSendReceiveString() {
        val n = NativeApi()
        val value = n.sendReceiveString("world")
        assertEquals("Hello world!", value)
    }

    @Test
    fun testSendStruct() {
        val n = NativeApi()
        val myStruct = FFIParameterStruct(
            123,
            "hi from Android",
            FFINestedParameterStruct(250)
        )
        val value = n.passStruct(myStruct, Callback())
        assertEquals(value, 1)
    }

    @Test
    fun testReturnStruct() {
        val n = NativeApi()
        val value = n.returnStruct(Callback())
        assertEquals(
            value,
            FFIParameterStruct(
                123, "my string parameter",
                FFINestedParameterStruct(123)
            )
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testCallCallback() = runBlockingTest {
        val n = NativeApi()
        val result = suspendCancellableCoroutine<String> { continuation ->
            n.callCallback(object : Callback() {
                override fun call(string: String) {
                    continuation.resume(string, onCancellation = {})
                }
            })
        }
        assertEquals(result, "hi!")
    }

    @Test
    fun testRegisterCallback() = runBlocking {
        val n = NativeApi()
        val result = suspendCancellableCoroutine<String> { continuation ->
            n.registerCallback(object : Callback() {
                override fun call(string: String) {
                    continuation.resume(string, onCancellation = {})
                }
            })
            n.triggerCallback("hello")
        }
        assertEquals("hello world!", result)
    }
}
