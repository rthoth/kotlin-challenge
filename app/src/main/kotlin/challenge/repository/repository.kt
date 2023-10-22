package challenge.repository

import org.flywaydb.core.Flyway

fun migrate(url: String) {
    Flyway.configure().dataSource(url, null, null).load().migrate()
}