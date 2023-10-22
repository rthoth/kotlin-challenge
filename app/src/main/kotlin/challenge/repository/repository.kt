package challenge.repository

import com.orientechnologies.orient.core.record.OElement
import com.orientechnologies.orient.core.record.ORecord
import com.orientechnologies.orient.core.record.OVertex
import com.orientechnologies.orient.core.sql.executor.OResultSet
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun <T : OElement> T.set(name: String, value: Any): T {
    this.setProperty(name, value)
    return this
}

fun <R> OElement.get(name: String): R {
    return this.getProperty(name)
}

fun <T : ORecord> T.store(): T {
    return this.save()
}

fun OResultSet.vertexHead(message: String = "Not found!"): OVertex {
    val value = this.vertexStream().findFirst()
    if (value.isPresent)
        return value.get()
    else
        throw IllegalStateException(message)
}

fun zonedDateTimeToString(zonedDateTime: ZonedDateTime): String =
    zonedDateTime.truncatedTo(ChronoUnit.MINUTES).toString()

fun stringToZonedDateTime(value: String): ZonedDateTime = ZonedDateTime.parse(value)