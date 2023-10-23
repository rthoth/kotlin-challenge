package challenge.repository

import challenge.ChallengeTest
import kotlinx.coroutines.runBlocking
import org.ktorm.database.Database

abstract class RepositoryTest<R> : ChallengeTest() {

    private val database = run {
        val database =
            Database.connect("jdbc:h2:mem:${javaClass.simpleName.lowercase()};DB_CLOSE_DELAY=-1;IGNORECASE=TRUE")
        migrate(database.url)
        database
    }

    protected abstract fun createRepository(database: Database): R

    protected fun <RET> withRepository(block: suspend (R, Database) -> RET): RET {
        return runBlocking {
            block(createRepository(database), database)
        }
    }
}