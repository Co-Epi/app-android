package org.coepi.android.ui.symptoms.breathless

import android.graphics.drawable.Drawable
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness

data class BreathlessViewData(val name: String, val icon: Drawable?, val isChecked: Boolean, val breathless: Breathlessness.Cause)