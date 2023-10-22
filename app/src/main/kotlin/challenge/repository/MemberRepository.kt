package challenge.repository

import challenge.Member
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.entity.Entity
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

interface MemberRepository {

    suspend fun add(member: Member): Member

    companion object {

        interface StoredEntity : Entity<StoredEntity> {
            companion object : Entity.Factory<StoredEntity>()

            var id: String
            var name: String
        }

        object Members : Table<StoredEntity>("MEMBER") {
            var id = varchar("ID").primaryKey().bindTo { it.id }
            var name = varchar("NAME").bindTo { it.name }
        }

        fun create(database: Database): MemberRepository = object : MemberRepository {

            init {

            }

            override suspend fun add(member: Member): Member {
                database.sequenceOf(Members).add(convert(member))
                return member
            }
        }

        private fun convert(member: Member) = StoredEntity {
            id = member.id
            name = member.name
        }
    }
}