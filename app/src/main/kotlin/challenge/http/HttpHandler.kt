package challenge.http

import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.marshalling.Marshaller
import akka.http.javadsl.model.RequestEntity
import akka.http.javadsl.model.StatusCode
import akka.http.javadsl.server.Directives.*
import akka.http.javadsl.server.Route
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

abstract class HttpHandler(private val executor: Executor) {

    fun <T> jacksonMarshaller(): Marshaller<T, RequestEntity> =
        Jackson.marshaller(objectMapper)

    fun <RET> completeWithSuspend(
        block: suspend () -> RET,
        onSuccess: (RET) -> Route,
        onFailure: (Throwable) -> Route = DefaultOnFailure
    ): Route {
        return onComplete(
            {
                CompletableFuture.supplyAsync({
                    runBlocking {
                        block()
                    }
                }, executor)
            },
            {
                try {
                    onSuccess(it.get())
                } catch (cause: Throwable) {
                    onFailure(cause)
                }
            }
        )
    }

    fun <A> completeAsJson(pair: Pair<StatusCode, A>): Route {
        return complete(pair.first, pair.second, jacksonMarshaller())
    }

    companion object {
        val DefaultOnFailure: (Throwable) -> Route = {
            failWith(it)
        }

        val objectMapper: JsonMapper = JsonMapper.builder()
            .addModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()
    }

}
