package io.github.turskyi.rxsubject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject

/**
Despite all examples are represented in MainActivity,
on practice MainActivity must be responsible only for showing the UI
and never implement business logic.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "Rx_subject"
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        behaviorSubject()
        publishSubject()
//         asyncSubject()
//         replaySubject()
    }

    /*
    It emits a last created element and all subsequent elements of the observed source
     when the observer joins it. Here, if a student enters the classroom,
     he wants to listen to the latest things (not from the beginning)
     that are taught by the professor. So that he gets an idea of the context.
      So here we will use BehaviorSubject.
     */
    private fun behaviorSubject() {
        // observable is a data provider
        val observable = Observable.just("Item 1", "Item 2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        /* we can use it when we need to make completely different actions with the same
        object at the same time
         */
        val behaviorSubject: BehaviorSubject<String> = BehaviorSubject.create()
        observable.subscribe(behaviorSubject)

        val firstObserver = getFirstObserver()

        behaviorSubject.subscribe(firstObserver)

        behaviorSubject.onNext("Item 1")
        behaviorSubject.onNext("Item 2")

        behaviorSubject.subscribe(getSecondObserver())
        behaviorSubject.onNext("Item 3")

        behaviorSubject.onComplete()
    }

    /* Emits all subsequent elements of the observed source at the time of subscription.
     Here, if a student enters the audience,
     he just wants to listen from the moment he enters the audience.
      And PublishSubject will be the best choice to use. */
    private fun publishSubject() {
        val publishSubject: PublishSubject<String> = PublishSubject.create()

        publishSubject.subscribe(getFirstObserver())
        publishSubject.onNext("Item 1")
        publishSubject.onNext("Item 2")

        publishSubject.subscribe(getSecondObserver())
        publishSubject.onNext("Item 3")
        publishSubject.onComplete()
    }

    /* It gives only the last value of the observed source.
    Here, if a student comes at any time to the audience,
    and he wants to hear only about the last thing (and only the last) that is taught,
     then here we will use Async. */
    private fun asyncSubject() {
        val observable = Observable.just("Item 1", "Item 2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        val asyncSubject: AsyncSubject<String> = AsyncSubject.create()
        observable.subscribe(asyncSubject)

        asyncSubject.subscribe(getFirstObserver())
        asyncSubject.subscribe(getSecondObserver())
    }

    /* It emits all elements of the Observable, regardless of when the subscriber subscribes,
     where if a student is late in entering the audience,
      he wants to listen to the lecture from the beginning, and so we have to use ReplaySubject. */
    private fun replaySubject() {
        val replaySubject: ReplaySubject<String> = ReplaySubject.create()

        replaySubject.subscribe(getFirstObserver())
        replaySubject.onNext("Item 1")
        replaySubject.onNext("Item 2")
        replaySubject.subscribe(getSecondObserver())
        replaySubject.onNext("Item 3")
        replaySubject.onComplete()
    }

    private fun getFirstObserver(): Observer<String> {
        return object : Observer<String> {

            override fun onNext(t: String) {
                Log.i(TAG, "First observer onNext $t")
            }

            override fun onComplete() {
                Log.i(TAG, "First observer onComplete")
            }

            override fun onError(e: Throwable) {
                Log.i(TAG, "onError observer")
            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }
        }
    }

    private fun getSecondObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {

            override fun onNext(t: String) {
                Log.i(TAG, "Second observer onNext $t")
            }

            override fun onComplete() {
                Log.i(TAG, "Second observer onComplete")
            }

            override fun onError(e: Throwable) {
                Log.i(TAG, "onError observer")
            }
        }
    }

    private fun getThirdObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {

            override fun onNext(t: String) {
                Log.i(TAG, "Third observer onNext $t")
            }

            override fun onComplete() {
                Log.i(TAG, "Third observer onComplete")
            }

            override fun onError(e: Throwable) {
                Log.i(TAG, "onError observer")
            }
        }
    }

    private fun getFourthObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {

            override fun onNext(t: String) {
                Log.i(TAG, "Fourth observer onNext $t")
            }

            override fun onComplete() {
                Log.i(TAG, "Fourth observer onComplete")
            }

            override fun onError(e: Throwable) {
                Log.i(TAG, "onError observer")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
/*
 Output:
2021-04-30 11:25:42.564 7091-7091/io.github.turskyi.rxsubject I/Rx_subject: First observer onNext Item 1
2021-04-30 11:25:42.564 7091-7091/io.github.turskyi.rxsubject I/Rx_subject: First observer onNext Item 2
2021-04-30 11:25:42.565 7091-7091/io.github.turskyi.rxsubject I/Rx_subject: First observer onNext Item 3
2021-04-30 11:25:42.565 7091-7091/io.github.turskyi.rxsubject I/Rx_subject: Second observer onNext Item 3
2021-04-30 11:25:42.565 7091-7091/io.github.turskyi.rxsubject I/Rx_subject: First observer onComplete
2021-04-30 11:25:42.565 7091-7091/io.github.turskyi.rxsubject I/Rx_subject: Second observer onComplete
 */
