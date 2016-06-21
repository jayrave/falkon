package com.jayrave.falkon.engine.jdbc

import java.sql.Connection

/**
 * A convenience class that manages acquiring & releasing [Connection]s no matter whether
 * a transaction is actively going on or not. When using implementations of this interface,
 * any connection acquired should not be explicitly closed. It should just be passed back to
 * [releaseConnection] when done
 */
internal interface ConnectionManager {
    fun acquireConnection(): Connection
    fun releaseConnection(connection: Connection)
}