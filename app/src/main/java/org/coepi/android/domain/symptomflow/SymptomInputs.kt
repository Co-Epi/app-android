package org.coepi.android.domain.symptomflow

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

enum class SymptomId { COUGH, BREATHLESSNESS, FEVER, MUSCLE_ACHES, LOSS_SMELL_OR_TASTE, DIARRHEA,
    RUNNY_NOSE, OTHER, NONE }

@Parcelize
data class SymptomInputs(
    val cough: Cough? = null,
    val breathlessness: Breathlessness? = null,
    val fever: Fever? = null
) : Parcelable {

    @Parcelize
    data class Cough(
        val type: Type? = null,
        val days: Days? = null,
        val status: Status? = null

    ) : Parcelable {
        enum class Type { WET, DRY }
        enum class Status {
            BETTER_AND_WORSE_THROUGH_DAY, WORSE_WHEN_OUTSIDE, SAME_OR_STEADILY_WORSE
        }

        @Parcelize
        data class Days(val value: Int) : Parcelable
    }

    @Parcelize
    data class Breathlessness(
        val cause: Cause? = null
    ) : Parcelable {
        enum class Cause {
            LEAVING_HOUSE_OR_DRESSING, WALKING_YARDS_OR_MINS_ON_GROUND, GROUND_OWN_PACE,
            HURRY_OR_HILL, EXERCISE
        }
    }

    @Parcelize
    data class Fever(
        val days: Days? = null,
        val takenTemperatureToday: Boolean? = null,
        val temperatureSpot: TemperatureSpot? = null,
        val highestTemperature: Temperature? = null
    ) : Parcelable {
        @Parcelize
        data class Days(val value: Int) : Parcelable

        sealed class TemperatureSpot : Parcelable {
            @Parcelize
            object Mouth : TemperatureSpot(), Parcelable

            @Parcelize
            object Ear : TemperatureSpot(), Parcelable

            @Parcelize
            object Armpit : TemperatureSpot(), Parcelable

            @Parcelize
            data class Other(val description: String) : TemperatureSpot(), Parcelable
        }
    }
}

@Parcelize
data class Temperature(val value: Float): Parcelable
