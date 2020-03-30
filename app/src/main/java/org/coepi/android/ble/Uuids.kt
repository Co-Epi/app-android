package org.coepi.android.ble

import java.util.UUID

object Uuids {
    val CoEpiServiceUUID = "BC908F39-52DB-416F-A97E-6EAC29F59CA8"
    val service: UUID = UUID.fromString(CoEpiServiceUUID)
    val characteristic: UUID = UUID.fromString("2ac35b0b-00b5-4af2-a50e-8412bcb94285")
}
