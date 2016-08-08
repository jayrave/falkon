package com.jayrave.falkon.engine

import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.util.*

class DbEventsManagerTest {

    @Test
    fun testEventFiredOutsideTransactionIsDelivered() {
        val eventListener = DbEventListenerForTest()
        val dbEventsManager = DbEventsManager() { false }
        dbEventsManager.registerDbEventListener(eventListener)

        // Assert no event is delivered on just registering
        assertThat(eventListener.singleEvents).isEmpty()

        // Fire event
        val dbEvent = DbEvent.forInsert("example table")
        dbEventsManager.onEvent(dbEvent)

        // Assert event is delivered
        assertThat(eventListener.singleEvents).containsOnly(dbEvent)
        assertThat(eventListener.multiEventsList).isEmpty()
    }


    @Test
    fun testEventsFiredInsideTransactionAreDeliveredOnCommit() {
        val eventListener = DbEventListenerForTest()
        val dbEventsManager = DbEventsManager() { true }
        dbEventsManager.registerDbEventListener(eventListener)

        // Assert no event is delivered on just registering
        assertThat(eventListener.multiEventsList).isEmpty()

        // Fire events
        val dbEvent1 = DbEvent.forInsert("example table 1")
        val dbEvent2 = DbEvent.forUpdate("example table 2")
        val dbEvent3 = DbEvent.forDelete("example table 3")
        dbEventsManager.onEvent(dbEvent1)
        dbEventsManager.onEvent(dbEvent2)
        dbEventsManager.onEvent(dbEvent3)

        // Assert events are not delivered yet
        assertThat(eventListener.multiEventsList).isEmpty()

        // Inform about transaction commit
        dbEventsManager.onTransactionCommittedSuccessfully()

        // Assert events are delivered
        assertThat(eventListener.singleEvents).isEmpty()
        assertThat(eventListener.multiEventsList).hasSize(1)
        assertThat(eventListener.multiEventsList.first()).containsOnly(dbEvent1, dbEvent2, dbEvent3)
    }


    @Test
    fun testEventsFiredInsideTransactionAreDiscardedOnRollback() {
        val eventListener = DbEventListenerForTest()
        val dbEventsManager = DbEventsManager() { true }
        dbEventsManager.registerDbEventListener(eventListener)

        // Assert no event is delivered on just registering
        assertThat(eventListener.multiEventsList).isEmpty()

        // Fire event
        val dbEvent1 = DbEvent.forInsert("example table 1")
        dbEventsManager.onEvent(dbEvent1)

        // Assert events are not delivered yet
        assertThat(eventListener.multiEventsList).isEmpty()

        // Inform about transaction rollback
        dbEventsManager.onTransactionRolledBack()

        // Assert no events are delivered
        assertThat(eventListener.singleEvents).isEmpty()
        assertThat(eventListener.multiEventsList).isEmpty()

        // Fire event
        val dbEvent2 = DbEvent.forUpdate("example table 2")
        dbEventsManager.onEvent(dbEvent2)

        // Assert events are not delivered yet
        assertThat(eventListener.multiEventsList).isEmpty()

        // Inform about transaction commit
        dbEventsManager.onTransactionCommittedSuccessfully()

        // Assert only appropriate event is delivered
        assertThat(eventListener.singleEvents).isEmpty()
        assertThat(eventListener.multiEventsList).hasSize(1)
        assertThat(eventListener.multiEventsList.first()).containsOnly(dbEvent2)
    }


    @Test
    fun testEventsFiredOutsideTransactionsAreNotDeliveredToUnregisteredListeners() {
        val eventListener = DbEventListenerForTest()
        val dbEventsManager = DbEventsManager() { false }
        dbEventsManager.registerDbEventListener(eventListener)
        dbEventsManager.unregisterDbEventListener(eventListener)

        // Fire event
        val dbEvent = DbEvent.forInsert("example table")
        dbEventsManager.onEvent(dbEvent)

        // Assert no events are delivered to unregistered listener
        assertThat(eventListener.singleEvents).isEmpty()
        assertThat(eventListener.multiEventsList).isEmpty()
    }


    @Test
    fun testEventsFiredInsideTransactionsAreNotDeliveredToUnregisteredListeners() {
        val eventListener1 = DbEventListenerForTest()
        val eventListener2 = DbEventListenerForTest()
        val dbEventsManager = DbEventsManager() { true }
        dbEventsManager.registerDbEventListener(eventListener1)
        dbEventsManager.registerDbEventListener(eventListener2)

        // Unregister the first event listener
        dbEventsManager.unregisterDbEventListener(eventListener1)

        // Fire event
        val dbEvent = DbEvent.forInsert("example table")
        dbEventsManager.onEvent(dbEvent)

        // Unregister the second event listener too
        dbEventsManager.unregisterDbEventListener(eventListener2)

        // Inform about transaction commit
        dbEventsManager.onTransactionCommittedSuccessfully()

        // Assert no events are delivered to unregistered listeners
        assertThat(eventListener1.singleEvents).isEmpty()
        assertThat(eventListener1.multiEventsList).isEmpty()
        assertThat(eventListener2.singleEvents).isEmpty()
        assertThat(eventListener2.multiEventsList).isEmpty()
    }



    private class DbEventListenerForTest : DbEventListener {

        val singleEvents = ArrayList<DbEvent>()
        val multiEventsList = ArrayList<Iterable<DbEvent>>()

        override fun onEvent(dbEvent: DbEvent) {
            singleEvents.add(dbEvent)
        }

        override fun onEvents(dbEvents: Iterable<DbEvent>) {
            multiEventsList.add(dbEvents)
        }
    }
}