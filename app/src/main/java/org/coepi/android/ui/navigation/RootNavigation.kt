package org.coepi.android.ui.navigation

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create

class RootNavigation {
    val navigationCommands: PublishSubject<NavigationCommand> = create()

    fun navigate(command: NavigationCommand) {
        navigationCommands.onNext(command)
    }
}
