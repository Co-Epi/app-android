package org.coepi.android.ui.symptoms.cough

import org.coepi.android.domain.model.Symptom

data class CoughStatusViewData(val name: String, val isChecked: Boolean, val symptom: Symptom)