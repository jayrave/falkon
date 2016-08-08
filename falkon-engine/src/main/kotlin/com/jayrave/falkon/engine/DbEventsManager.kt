package com.jayrave.falkon.engine

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * This class takes care of registering / un-registering [DbEventListener]s & forwarding
 * the reported [DbEvent]s. This class is transaction aware i.e., if [DbEvent]s that are
 * reported through [onEvent] are fired from inside a transaction, they are queued & either
 * dispatched or discarded depending on the following call to either
 * [onTransactionCommittedSuccessfully] or [onTransactionRolledBack]. [DbEvent]s that
 * are fired from outside a transaction are dispatched immediately
 *
 * *Note:*
 *      - It is assumed that only one transaction can be active per thread at a time
 *      - All events must be forwarded to this class from the same thread they were fired on
 */
class DbEventsManager(private val isInTransactionInformer: () -> Boolean) {

    private val dbEventListeners = Collections.newSetFromMap<DbEventListener>(ConcurrentHashMap())
    private val dbEventsFiredInsideTransactions = object : ThreadLocal<LinkedList<DbEvent>>() {
        override fun initialValue(): LinkedList<DbEvent> {
            return LinkedList()
        }
    }

    /**
     * If the listener is already, this is a no-op. Can be called from any thread
     */
    fun registerDbEventListener(dbEventListener: DbEventListener) {
        dbEventListeners.add(dbEventListener)
    }

    /**
     * If the listener isn't already, this is a no-op. Can be called from any thread
     */
    fun unregisterDbEventListener(dbEventListener: DbEventListener) {
        dbEventListeners.remove(dbEventListener)
    }


    /**
     * If the fired [dbEvent] is from inside a transaction, this method should be called
     * from the same thread in which the transaction is being executed
     */
    fun onEvent(dbEvent: DbEvent) {
        if (isInTransactionInformer.invoke()) {
            // A transaction is currently active in this thread. Just queue this event.
            // It will either be reported if the transaction completes successfully or
            // discarded if it fails
            dbEventsFiredInsideTransactions.get().add(dbEvent)

        } else {
            // Event got fired outside of transaction. To be defensive, fire all the events
            // that were queued from the transaction that last got executed in this thread
            // (may be for some reason transaction end wasn't reported)
            fireAndClearEventsQueuedForTransactionInThisThread()

            // No active transactions. Just go ahead & inform the listeners
            dbEventListeners.forEach { it.onEvent(dbEvent) }
        }
    }


    /**
     * Should be called from the same thread in which the transaction was executed
     */
    fun onTransactionCommittedSuccessfully() {
        // Transaction was successful! Dispatch all events to the listeners
        fireAndClearEventsQueuedForTransactionInThisThread()
    }


    /**
     * Should be called from the same thread in which the transaction was executed
     */
    fun onTransactionRolledBack() {
        // Transaction wasn't successful! Drop all the queued events
        clearEventsQueuedForTransactionInThisThread()
    }


    private fun fireAndClearEventsQueuedForTransactionInThisThread() {
        // Extract events
        val dbEventsFiredInsideTransaction = dbEventsFiredInsideTransactions.get()

        // Let the listeners know about the events & clear the events
        if (dbEventsFiredInsideTransaction.isNotEmpty()) {
            clearEventsQueuedForTransactionInThisThread()
            dbEventListeners.forEach { it.onEvents(dbEventsFiredInsideTransaction) }
        }
    }


    private fun clearEventsQueuedForTransactionInThisThread() {
        dbEventsFiredInsideTransactions.remove()
    }
}