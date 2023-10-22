package challenge.repository

import challenge.MobilePhone
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

interface MobilePhoneRepository {

    suspend fun add(mobilePhone: MobilePhone): MobilePhone

    companion object {

        interface StoredEntity : Entity<StoredEntity> {

            companion object : Entity.Factory<StoredEntity>()

            var id: String
            var model: String
        }

        object MobilePhones : Table<StoredEntity>("MOBILE_PHONE") {
            val id = varchar("ID").primaryKey().bindTo { it.id }
            val model = varchar("MODEL").bindTo { it.model }
        }

        fun create(database: Database): MobilePhoneRepository = object : MobilePhoneRepository {

            override suspend fun add(mobilePhone: MobilePhone): MobilePhone {
                database.sequenceOf(MobilePhones).add(convert(mobilePhone))
                return mobilePhone
            }
        }

        private fun convert(mobilePhone: MobilePhone) = StoredEntity {
            id = mobilePhone.id
            model = mobilePhone.model
        }
    }
}