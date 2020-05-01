package org.tcncoalition.tcnclient.bluetooth

import kotlin.math.pow

//  Created by Zsombor SZABO on 18/04/2020.

const val measuredRSSIAtOneMeterDefault: Int = -67

fun getMeasuredRSSIAtOneMeter(txPowerLevel: Int?, hintIsAndroid: Boolean = false): Int {

    var effectiveTxPowerLevel = txPowerLevel
    if (effectiveTxPowerLevel == null) {
        effectiveTxPowerLevel = when {
            !hintIsAndroid -> 11
            else -> 12
        }
    }

    if (effectiveTxPowerLevel < 0) {
        effectiveTxPowerLevel += 20
    }

    return when (effectiveTxPowerLevel) {
        in 12..20 -> measuredRSSIAtOneMeterDefault
        in 9..12 -> -71
        else -> -86
    }
}

fun getEstimatedDistanceMeters(
    RSSI: Int,
    measuredRSSIAtOneMeter: Int = measuredRSSIAtOneMeterDefault,
    environmentalFactor: Double = 2.0
): Double {
    if (RSSI >= 20.0) return -1.0
    if (environmentalFactor !in 2.0..4.0) return -1.0
    return 10.0.pow((measuredRSSIAtOneMeter - RSSI) / (10.0 * environmentalFactor))
}