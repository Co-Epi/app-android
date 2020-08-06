package org.coepi.android.system

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import org.coepi.android.system.log.log
import java.util.Locale

interface LocaleProvider {
    val locale: Observable<Locale>
    fun update()
}

class LocaleProviderImpl(
    private val context: Context
): LocaleProvider {
    override val locale: BehaviorSubject<Locale> = createDefault(getPreferredLocale())

    override fun update() {
        locale.onNext(getPreferredLocale().also {
            log.i("Updating locale to: $it")
        })
    }

    private fun getPreferredLocale(): Locale =
        if (SDK_INT >= N) {
            context.resources.configuration.locales.get(0) ?: {
                log.w("Locales is empty. Falling back to (deprecated) main locale.")
                context.resources.configuration.locale
            }()
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
}
