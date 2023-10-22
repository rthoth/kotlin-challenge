package challenge.repository

import challenge.MobilePhone

interface MobilePhoneRepository {

    suspend fun add(mobilePhone: MobilePhone): MobilePhone

    companion object {

        fun create(session: Session) = object : MobilePhoneRepository {

            override suspend fun add(mobilePhone: MobilePhone): MobilePhone {
                return session.attempt { database ->
                    database.newVertex(Classes.MobilePhone)
                        .set("id", mobilePhone.id)
                        .set("model", mobilePhone.model)
                        .store()
                    mobilePhone
                }
            }
        }
    }
}