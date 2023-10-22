package challenge.module

import challenge.repository.MemberRepository
import challenge.repository.MobilePhoneRepository
import challenge.repository.Session
import com.orientechnologies.orient.core.config.OGlobalConfiguration
import com.orientechnologies.orient.core.db.ODatabaseType
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig

class RepositoryModule {

    private val session = run {
        val database = "challenge"

        val orientDB = OrientDB(
            "embedded:/tmp",
            OrientDBConfig.builder().addConfig(OGlobalConfiguration.CREATE_DEFAULT_USERS, true).build()
        )

        if (!orientDB.exists(database)) {
            orientDB.create(database, ODatabaseType.PLOCAL)
        }
        Session.create(orientDB, database, "admin", "admin")
    }


    val memberRepository = MemberRepository.create(session)
    val mobilePhoneRepository = MobilePhoneRepository.create(session)

}

