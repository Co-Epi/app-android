package org.tcncoalition.tcnclient

import java.util.UUID

//  Created by Zsombor Szabo on 10/04/2020.

/** An object that contains the constants defined in the TCN protocol. */
object TcnConstants {

    /** The domain name in reverse dot notation of the TCN coalition. */
    const val DOMAIN_NAME_IN_REVERSE_DOT_NOTATION_STRING = "org.tcn-coalition"

    /** The string representation of the 0xC019 16-bit UUID of the BLE service. */
    private const val UUID_SERVICE_STRING = "0000C019-0000-1000-8000-00805F9B34FB"
    val UUID_SERVICE: UUID = UUID.fromString(UUID_SERVICE_STRING)

    /**
     * The string representation of the 128-bit UUID of the BLE characteristic exposed by the
     * primary peripheral service in connection-oriented mode.
     * */
    private const val UUID_CHARACTERISTIC_STRING = "D61F4F27-3D6B-4B04-9E46-C9D2EA617F62"
    val UUID_CHARACTERISTIC: UUID = UUID.fromString(UUID_CHARACTERISTIC_STRING)

    /** The time interval in minutes how often the temporary contact number changes.*/
    const val TCN_CHANGE_PERIOD: Long = 15

    /** The byte-length of the temporary contact number. */
    const val TEMPORARY_CONTACT_NUMBER_LENGTH: Int = 16

    const val TCK_BYTES_LENGTH = 32;
    const val TCN_LENGTH = 16;

    private const val H_TCN_DOMAIN_SEPARATOR_STRING = "H_TCN"
    val H_TCN_DOMAIN_SEPARATOR = H_TCN_DOMAIN_SEPARATOR_STRING.toByteArray(Charsets.UTF_8)

    private const val H_TCK_DOMAIN_SEPARATOR_STRING = "H_TCK"
    val H_TCK_DOMAIN_SEPARATOR = H_TCK_DOMAIN_SEPARATOR_STRING.toByteArray(Charsets.UTF_8)
}
