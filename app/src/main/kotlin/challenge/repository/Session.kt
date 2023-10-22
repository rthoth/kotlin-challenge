package challenge.repository

import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.document.ODatabaseDocument

interface Session {

    suspend fun <RET> attempt(block: suspend (ODatabaseDocument) -> RET): RET

    companion object {

        fun create(orientDB: OrientDB, database: String, user: String, password: String) = object : Session {

            override suspend fun <RET> attempt(block: suspend (ODatabaseDocument) -> RET): RET {
                return orientDB.open(database, user, password).use { database ->
                    block.invoke(database)
                }
            }
        }
    }
}