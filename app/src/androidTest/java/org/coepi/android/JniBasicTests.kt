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
class JniBasicTests {

    @Test
    fun testSendReceiveString() {
        val n = org.coepi.android.api.NativeApi()
        val value = n.sendReceiveString("world")
        assertEquals("Hello world!", value)
    }

    @Test
    fun testSendStruct() {
        val n = org.coepi.android.api.NativeApi()
        val myStruct = org.coepi.android.api.FFIParameterStruct(
            123,
            "hi from Android",
            org.coepi.android.api.FFINestedParameterStruct(250)
        )
        val value = n.passStruct(myStruct)
        assertEquals(value, 1)
    }

    @Test
    fun testReturnStruct() {
        val n = org.coepi.android.api.NativeApi()
        val value = n.returnStruct()
        assertEquals(
            value,
            org.coepi.android.api.FFIParameterStruct(
                123, "my string parameter",
                org.coepi.android.api.FFINestedParameterStruct(123)
            )
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testCallCallback() = runBlockingTest {
        val n = org.coepi.android.api.NativeApi()
        val result = suspendCancellableCoroutine<String> { continuation ->
            n.callCallback(object : org.coepi.android.api.Callback() {
                override fun call(string: String) {
                    continuation.resume(string, onCancellation = {})
                }
            })
        }
        assertEquals(result, "hi!")
    }

    @Test
    fun testRegisterCallback() = runBlocking {
        val n = org.coepi.android.api.NativeApi()
        val result = suspendCancellableCoroutine<String> { continuation ->
            n.registerCallback(object : org.coepi.android.api.Callback() {
                override fun call(string: String) {
                    continuation.resume(string, onCancellation = {})
                }
            })
            n.triggerCallback("hello")
        }
        assertEquals("hello world!", result)
    }
}
