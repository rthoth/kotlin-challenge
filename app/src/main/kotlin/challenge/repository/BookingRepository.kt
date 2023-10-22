package challenge.repository

import challenge.Booking
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime

interface BookingRepository {

    suspend fun add(booking: Booking): Booking

    suspend fun searchByMobilePhone(
        phoneId: String, starting: ZonedDateTime, ending: ZonedDateTime, timeGap: Duration
    ): List<Booking>

    companion object {
        interface StoredEntity : Entity<StoredEntity> {

            companion object : Entity.Factory<StoredEntity>()

            var id: String
            var phoneId: String
            var memberId: String
            var starting: Instant
            var ending: Instant
        }

        object Bookings : Table<StoredEntity>("BOOKING") {
            var id = varchar("ID").primaryKey().bindTo { it.id }
            var phoneId = varchar("PHONE_ID").bindTo { it.phoneId }
            var memberId = varchar("MEMBER_ID").bindTo { it.memberId }
            var starting = timestamp("STARTING").bindTo { it.starting }
            var ending = timestamp("ENDING").bindTo { it.ending }
        }

        fun create(database: Database): BookingRepository = object : BookingRepository {

            override suspend fun add(booking: Booking): Booking {
                database.sequenceOf(Bookings).add(convert(booking))
                return booking
            }

            override suspend fun searchByMobilePhone(
                phoneId: String, starting: ZonedDateTime, ending: ZonedDateTime, timeGap: Duration
            ): List<Booking> {
                val newStarting = starting.minus(timeGap).toInstant()
                val newEnding = ending.plus(timeGap).toInstant()

                return database
                    .sequenceOf(Bookings)
                    .filter {
                        ((it.starting gte newStarting) and (it.starting lt newEnding)) or ((it.ending gt newStarting) and (it.ending lte newEnding)) or ((it.starting lte newStarting) and (it.ending gte newEnding))
                    }
                    .map { convert(it) }
                    .toList()
            }

            private fun convert(booking: Booking) = StoredEntity {
                id = booking.id
                phoneId = booking.mobilePhoneId
                memberId = booking.memberId
                starting = convertToTimestamp(booking.starting)
                ending = convertToTimestamp(booking.ending)
            }

            private fun convert(stored: StoredEntity) = Booking(
                id = stored.id,
                mobilePhoneId = stored.phoneId,
                memberId = stored.memberId,
                starting = convertToZonedDateTime(stored.starting),
                ending = convertToZonedDateTime(stored.ending)
            )
        }
    }
}