package challenge

import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

interface InstantFactory {

    fun now(): Instant

    companion object : InstantFactory {

        override fun now(): Instant = Instant.now(Clock.systemUTC()).truncatedTo(ChronoUnit.SECONDS)
    }
}