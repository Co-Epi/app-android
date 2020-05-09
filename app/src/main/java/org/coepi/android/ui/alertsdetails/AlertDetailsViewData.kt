package org.coepi.android.ui.alertsdetails

import androidx.annotation.DrawableRes
import org.coepi.android.domain.model.Symptom
import org.coepi.android.domain.symptomflow.SymptomId

data class AlertDetailsSymptomViewData(@DrawableRes val icon: Int, val symptomName: String,
                                       val symptom: SymptomId
)
