package org.coepi.android.extensions.rx

import io.reactivex.Completable
import io.reactivex.Observable

fun <T> Completable.toObservable(value: T): Observable<T> =
    andThen(Observable.just(value))

fun Completable.toUnitObservable(): Observable<Unit> =
    toObservable(Unit)
