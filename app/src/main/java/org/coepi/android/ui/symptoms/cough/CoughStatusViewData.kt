package org.coepi.android.ui.symptoms.cough

import org.coepi.android.domain.symptomflow.SymptomInputs.Cough

data class CoughStatusViewData(val name: String, val isChecked: Boolean, val status: Cough.Status)