package com.jayrave.falkon.engine.rxjava1

import com.jayrave.falkon.engine.*
import rx.Observable
import rx.Scheduler
import rx.Subscriber
import rx.functions.Func1

/**
 * Creates an [Observable] which will notify the subscribers with an appropriate
 * [CompiledStatement] that represents the compiled query for passed in [rawSql] &
 * [arguments], every time there is a [DbEvent] associated with the passed in
 * [tableNames]
 *
 * *NOTE:*
 *      - Subscribers are responsible for closing the [CompiledStatement]
 *      - A statement is compiled on subscription
 *      - *Unsubscribe* when done, to clean up
 *
 * @param tableNames names of tables this [rawSql] is concerned with
 * @param rawSql raw SELECT statement
 * @param arguments to be bound to [rawSql]
 * @param scheduler to compile the query & deliver result on
 *
 * @return observable of compiled query for the given [rawSql] & [arguments]
 */
fun Engine.createCompiledQueryObservable(
        tableNames: Iterable<String>, rawSql: String, arguments: Iterable<Any>?,
        scheduler: Scheduler): Observable<CompiledStatement<Source>> {

    return Observable.defer {
        val predicate = Func1<Iterable<DbEvent>, Boolean> {
            it.any { tableNames.contains(it.tableName) }
        }

        createDbEventObservable(scheduler)
                .filter(predicate) // Only allow events for tables this query is concerned with
                .startWith(emptyList<DbEvent>()) // To execute the query on subscription
                .map { compileQuery(tableNames, rawSql).bindAll(arguments) }
    }
}


/**
 * An [Observable] that notifies the subscribers about [DbEvent]s
 *
 * *NOTE:* *Unsubscribe* when done, to clean up
 *
 * @param scheduler to deliver the events on
 * @return observable of db events
 *
 * @see [Engine.registerDbEventListener]
 */
fun Engine.createDbEventObservable(scheduler: Scheduler): Observable<Iterable<DbEvent>> {
    val onSubscribe = DbEventForwardingOnSubscribe(this)
    return Observable
            .create(onSubscribe)
            .observeOn(scheduler)
            .doOnUnsubscribe {
                onSubscribe.unregisterDbEventListener() // Needn't listen for events anymore
            }
}


/**
 * A [Observable.OnSubscribe] that listens for new db events & forwards it to the subscriber.
 * *NOTE:* Make sure to call [unregisterDbEventListener] to unregister the listener ASAP
 */
private class DbEventForwardingOnSubscribe(private val engine: Engine) :
        Observable.OnSubscribe<Iterable<DbEvent>> {

    private var listener: DbEventListener? = null

    override fun call(t: Subscriber<in Iterable<DbEvent>>) {
        val tempListener = object : DbEventListener {
            override fun onEvent(dbEvent: DbEvent) = onEvents(listOf(dbEvent))
            override fun onEvents(dbEvents: Iterable<DbEvent>) {
                when {
                    t.isUnsubscribed -> unregisterDbEventListener()
                    else -> t.onNext(dbEvents)
                }
            }
        }

        listener = tempListener // Store listener to close later
        engine.registerDbEventListener(tempListener) // Start listening for events
    }


    fun unregisterDbEventListener() {
        val tempListener = listener
        if (tempListener != null) {
            engine.unregisterDbEventListener(tempListener)
            listener = null
        }
    }
}