package challenge.repository

import org.flywaydb.core.Flyway
import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime

fun migrate(url: String) {
    Flyway.configure().dataSource(url, null, null).load().migrate()
}

fun convertToTimestamp(input: ZonedDateTime) = input.toInstant()

fun converToToZonedDateTime(input: Instant) = ZonedDateTime.ofInstant(input, Clock.systemUTC().zone)