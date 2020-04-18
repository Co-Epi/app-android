package org.coepi.android.ui.alertsdetails

import androidx.annotation.DrawableRes
import org.coepi.android.domain.model.Symptom

data class AlertDetailsSymptomViewData(@DrawableRes val icon: Int, val symptomName: String,
                                       val symptom: Symptom)
