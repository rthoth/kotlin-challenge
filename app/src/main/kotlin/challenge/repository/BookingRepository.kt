package challenge.repository

import challenge.Booking
import com.orientechnologies.orient.core.record.OEdge
import java.time.Duration
import java.time.ZonedDateTime

interface BookingRepository {

    suspend fun add(booking: Booking): Booking

    suspend fun searchByMobilePhone(
        phoneId: String,
        starting: ZonedDateTime,
        ending: ZonedDateTime,
        timeGap: Duration
    ): List<Booking>

    companion object {

        fun create(session: Session): BookingRepository = object : BookingRepository {

            override suspend fun add(booking: Booking): Booking {
                return session.attempt {
                    val mobileVertex =
                        it.query("SELECT FROM ${Classes.MobilePhone} WHERE id = ?", booking.mobilePhoneId)
                            .vertexHead("There is no mobile=${booking.mobilePhoneId}!")

                    val memberVertex =
                        it.query("SELECT FROM ${Classes.Member} WHERE id = ?", booking.memberId)
                            .vertexHead("There is no member=${booking.memberId}!")

                    mobileVertex.addEdge(memberVertex, Classes.Booking)
                        .set("id", booking.id)
                        .set("starting", zonedDateTimeToString(booking.starting))
                        .set("ending", zonedDateTimeToString(booking.ending))
                        .store()

                    booking
                }
            }

            override suspend fun searchByMobilePhone(
                mobilePhoneId: String,
                starting: ZonedDateTime,
                ending: ZonedDateTime,
                timeGap: Duration
            ): List<Booking> {
                return session.attempt {
                    val params = mapOf(
                        "mobilePhoneId" to mobilePhoneId,
                        "starting" to zonedDateTimeToString(starting.minus(timeGap)),
                        "ending" to zonedDateTimeToString(ending.plus(timeGap))
                    )

                    it.query(
                        "SELECT FROM (SELECT outE('${Classes.Booking}') FROM ${Classes.MobilePhone} WHERE id = :mobilePhoneId) WHERE (starting >= :starting and starting < :ending) or (ending < :ending and ending >= :starting) or (starting <= :starting and ending >= :ending)",
                        params
                    ).stream()
                        .map { toBooking(it as OEdge) }
                        .toList()
                }
            }

            private fun toBooking(vertex: OEdge): Booking {
                return Booking(
                    id = vertex.get("id"),
                    mobilePhoneId = vertex.get("phoneId"),
                    memberId = vertex.get("memberId"),
                    starting = stringToZonedDateTime(vertex.get("starting")),
                    ending = stringToZonedDateTime(vertex.get("ending"))
                )
            }
        }
    }
}