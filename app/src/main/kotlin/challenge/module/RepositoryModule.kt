package challenge.module

import challenge.repository.MemberRepository
import challenge.repository.MobilePhoneRepository
import org.ktorm.database.Database

class RepositoryModule {

    val database = Database.connect("jdbc:h2:mem:challenge;DB_CLOSE_DELAY=-1")

    val memberRepository = MemberRepository.create(database)
    val mobilePhoneRepository = MobilePhoneRepository.create(database)

}

