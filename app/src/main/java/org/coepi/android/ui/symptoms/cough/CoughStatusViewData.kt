package org.coepi.android.ui.symptoms.cough

import org.coepi.core.domain.model.SymptomInputs.Cough

data class CoughStatusViewData(val name: String, val isChecked: Boolean, val status: Cough.Status)