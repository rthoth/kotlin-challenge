package challenge.repository

import challenge.ChallengeTest
import com.orientechnologies.orient.core.config.OGlobalConfiguration
import com.orientechnologies.orient.core.db.ODatabaseType
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig
import kotlinx.coroutines.runBlocking

abstract class RepositoryTest<R> : ChallengeTest() {

    protected abstract fun createRepository(session: Session): R

    protected fun <RET> testRepository(block: suspend (R, Session) -> RET): RET {
        val session = createSession()
        return runBlocking {
            block(createRepository(session), session)
        }
    }

    protected fun createSession(): Session {
        val orientDB = OrientDB(
            "memory:${javaClass.simpleName}",
            OrientDBConfig.builder().addConfig(OGlobalConfiguration.CREATE_DEFAULT_USERS, true).build()
        )
        if (!orientDB.exists("test"))
            orientDB.create("test", ODatabaseType.MEMORY)

        return Classes.prepare(
            Session.create(
                orientDB,
                "test",
                "admin",
                "admin"
            )
        )
    }
}