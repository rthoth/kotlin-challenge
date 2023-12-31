package challenge

import challenge.repository.migrate
import org.ktorm.database.Database

class RepositoryModule {

    private val database = run {
        val database = Database.connect("jdbc:h2:mem:challenge;DB_CLOSE_DELAY=-1")
        migrate(database.url)
        database
    }

    val mobilePhoneRepository = MobilePhoneRepository.create(database)

}

