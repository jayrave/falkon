package com.jayrave.falkon.engine.testLib

import com.jayrave.falkon.engine.DbEvent
import com.jayrave.falkon.engine.DbEventListener
import java.util.*

class DbEventListenerForTest : DbEventListener {

    val singleEvents = ArrayList<DbEvent>()
    val multiEventsList = ArrayList<Iterable<DbEvent>>()

    override fun onEvent(dbEvent: DbEvent) {
        singleEvents.add(dbEvent)
    }

    override fun onEvents(dbEvents: Iterable<DbEvent>) {
        multiEventsList.add(dbEvents)
    }
}