package challenge.fonoapi

import challenge.MobilePhone
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import java.util.*

interface FonoapiService {

    suspend fun get(mobilePhone: MobilePhone): Optional<DeviceEntity>

    companion object {

        /**
         * Fonoapi is not available, so it's a prototype of an implementation.
         */
        fun create(token: String): FonoapiService = object : FonoapiService {

            private val memoryCache = mutableMapOf<String, Optional<DeviceEntity>>()

            private val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json()
                }
            }

            // Here we would use a better cache machinery like a external database
            private suspend fun cache(
                mobilePhoneId: String,
                block: suspend () -> Optional<DeviceEntity>
            ): Optional<DeviceEntity> {
                if (!memoryCache.containsKey(mobilePhoneId)) {
                    memoryCache[mobilePhoneId] = block()
                }

                return memoryCache[mobilePhoneId]!!
            }

            override suspend fun get(mobilePhone: MobilePhone): Optional<DeviceEntity> {
                return cache(mobilePhone.id) {
                    try {
                        Optional.ofNullable(client.get("https://fonoapi.freshpixl.com/v1/getdevice") {
                            url {
                                parameters.append("device", mobilePhone.model)
                                parameters.append("token", token)
                            }
                            timeout {
                                requestTimeoutMillis = 5000
                            }
                        }.body<List<DeviceEntity>>().firstOrNull())
                    } catch (_: Throwable) {
                        Optional.empty()
                    }
                }
            }
        }
    }
}