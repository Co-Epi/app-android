package org.tcncoalition.tcnclient.crypto

import org.junit.Assert.*
import org.junit.Test
import org.tcncoalition.tcnclient.bytesToHex
import org.tcncoalition.tcnclient.hexToBytes

class TestVectors {
    /**
     * Generated from the reference implementation with
     * `cargo test generate_test_vectors -- --nocapture`.
     */
    @ExperimentalUnsignedTypes
    @Test
    fun referenceImpl() {
        val rak = ReportAuthorizationKey.fromByteArray(
            "577cfdae21fee71579211ab02c418ee0948bacab613cf69d0a4a5ae5a1557dbb".hexToBytes()
        )

        val expectedTckBytes = arrayOf(
            "df535b90ac99bec8be3a8add45ce77897b1e7cb1906b5cff1097d3cb142fd9d0",
            "25607e1398836b8882874bd7195a2829a506942c8d45d1e36f772d7d4c12d16e",
            "2bee15dd8e70aa9c4c8e43240eaa735d922984b33fda2a47f919ddd0d5a174cf",
            "67bcaf90bacf4a68eb9c05e433fbadef652082d3e9f1a144c0c33e6c48c9b42d",
            "a5a64f060f1b3b82c8977413b20a391053e339ec56383180efc1bb826bf65493",
            "c7e13775159649342247cea52125402da073a93ed9a36a9f8f813b96913ba1b3",
            "c8c79b595e82a9abbb04c6b16d09225433ab84d9c3c28d27736745d7d3e1d8f2",
            "4c96eb8375eb9afe693a1ef1f1c564676122c8484b3073914749a64d2f61b83a",
            "0a7a2f476f02dd720e88d5f4290656b28ca151919d67c408daa174bef8112b9e"
        )
        val expectedTcn = arrayOf(
            "f4350a4a33e30f2f568898fbe4c4cf34",
            "135eeaa6482b8852fea3544edf6eabf0",
            "d713ce68cf4127bcebde6874c4991e4b",
            "5174e6514d2086565e4ea09a45995191",
            "ccae4f2c3144ad1ed0c2a39613ef0342",
            "3b9e600991369bba3944b6e9d8fda370",
            "dc06a8625c08e946317ad4c89e6ee8a1",
            "9d671457835f2c254722bfd0de76dffc",
            "8b454d28430d3153a500359d9a49ec88"
        )

        var tck = rak.initialTemporaryContactKey

        for (i in 1..9) {
            assertEquals(i.toShort(), tck.index.short)
            assertEquals(expectedTckBytes[i - 1], tck.tckBytes.bytesToHex())
            assertEquals(expectedTcn[i - 1], tck.temporaryContactNumber.bytes.bytesToHex())

            tck = tck.ratchet()!!
        }

        val signedReport =
            rak.createReport(
                MemoType.CoEpiV1,
                "symptom data".toByteArray(Charsets.UTF_8),
                2.toUShort(),
                10.toUShort()
            )

        assertEquals(
            "fd8deb9d91a13e144ca5b0ce14e289532e040fe0bf922c6e3dadb1e4e2333c78df535b90ac99bec8be3a8add45ce77897b1e7cb1906b5cff1097d3cb142fd9d002000a00000c73796d70746f6d206461746131078ec5367b67a8c793b740626d81ba904789363137b5a313419c0f50b180d8226ecc984bf073ff89cbd9c88fea06bda1f0f368b0e7e88bbe68f15574482904",
            signedReport.toByteArray().bytesToHex()
        )
    }
}
