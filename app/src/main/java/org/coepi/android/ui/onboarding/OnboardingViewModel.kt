package org.coepi.android.ui.onboarding

import android.net.Uri
import android.net.Uri.parse
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.R.drawable.ic_intro_1
import org.coepi.android.R.drawable.ic_intro_2
import org.coepi.android.R.drawable.ic_intro_3
import org.coepi.android.R.string.link_faq
import org.coepi.android.R.string.link_privacy
import org.coepi.android.R.string.onboarding_card_content_1
import org.coepi.android.R.string.onboarding_card_content_2
import org.coepi.android.R.string.onboarding_card_content_3
import org.coepi.android.R.string.onboarding_card_content_4
import org.coepi.android.R.string.onboarding_card_title_1
import org.coepi.android.R.string.onboarding_card_title_2
import org.coepi.android.R.string.onboarding_card_title_3
import org.coepi.android.R.string.onboarding_card_title_4
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.system.Preferences
import org.coepi.android.system.PreferencesKey.SEEN_ONBOARDING
import org.coepi.android.system.Resources
import org.coepi.android.ui.common.ActivityFinisher
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.onboarding.OnboardingCardViewData.LargeCard
import org.coepi.android.ui.onboarding.OnboardingCardViewData.SmallCard
import org.coepi.android.ui.onboarding.OnboardingClickEvent.FaqClicked
import org.coepi.android.ui.onboarding.OnboardingClickEvent.JoinClicked
import org.coepi.android.ui.onboarding.OnboardingClickEvent.NextClicked
import org.coepi.android.ui.onboarding.OnboardingClickEvent.PrivacyLinkClicked

class OnboardingViewModel(
    private val rootNav: RootNavigation,
    private val preferences: Preferences,
    private val resources: Resources,
    private val activityFinisher: ActivityFinisher
) : ViewModel() {

    private val currentCardIndex: BehaviorSubject<Int> = createDefault(0)
    private val nextCardTrigger: PublishSubject<Unit> = create()
    private val backCardTrigger: PublishSubject<Unit> = create()
    private val openLinkSubject: PublishSubject<Uri> = create()

    val recyclerViewScrollPosition: LiveData<Int> = currentCardIndex.toLiveData()
    val openLink: LiveData<Uri> = openLinkSubject.toLiveData()

    val viewData: List<OnboardingCardViewData> = listOf(
        SmallCard(
            resources.getString(onboarding_card_title_1),
            resources.getString(onboarding_card_content_1),
            0,
            ic_intro_1
        ),
        SmallCard(
            resources.getString(onboarding_card_title_2),
            resources.getString(onboarding_card_content_2),
            1,
            ic_intro_2
        ),
        SmallCard(
            resources.getString(onboarding_card_title_3),
            resources.getString(onboarding_card_content_3),
            2,
            ic_intro_3
        ),
        LargeCard(
            resources.getString(onboarding_card_title_4),
            resources.getString(onboarding_card_content_4)
        )
    )

    var disposables = CompositeDisposable()

    init {
        disposables += nextCardTrigger.withLatestFrom(currentCardIndex).subscribe { (_, index) ->
            currentCardIndex.onNext(index + 1)
        }

        disposables += backCardTrigger.withLatestFrom(currentCardIndex).subscribe { (_, index) ->
            val newIndex = index - 1
            if (newIndex < 0) {
                // Case where user presses back and they are on the first onboarding tab
                activityFinisher.finish()
            } else {
                currentCardIndex.onNext(newIndex)
            }
        }
    }

    private fun onCloseClick() {
        preferences.putBoolean(SEEN_ONBOARDING, true)
        rootNav.navigate(Back)
    }

    fun onCardEvent(event: OnboardingClickEvent) {
        when (event) {
            is NextClicked -> nextCardTrigger.onNext(Unit)
            is PrivacyLinkClicked ->
                openLinkSubject.onNext(parse(resources.getString(link_privacy)))
            is JoinClicked -> onCloseClick()
            is FaqClicked -> openLinkSubject.onNext(parse(resources.getString(link_faq)))
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun onBack() {
        backCardTrigger.onNext(Unit)
    }
}
