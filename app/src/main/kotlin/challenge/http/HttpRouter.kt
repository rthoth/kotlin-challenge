package challenge.http

import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.Directives.*
import akka.http.javadsl.server.ExceptionHandler
import akka.http.javadsl.server.PathMatchers.segment
import akka.http.javadsl.server.RejectionHandler
import akka.http.javadsl.server.Route

class HttpRouter(
    private val bookingHandler: BookingHandler
) {

    fun create(): Route = run {
        concat(
            path(segment("mobile").slash(segment()).slash("booking")) { mobilePhoneId ->
                concat(
                    post { bookingHandler.booked(mobilePhoneId) },
                    delete { bookingHandler.returned(mobilePhoneId) }
                )
            },
            path(segment("mobile")) {
                get { bookingHandler.info() }
            }
        ).seal(
            RejectionHandler.defaultHandler(), ExceptionHandler
                .newBuilder()
                .match(Throwable::class.java) { throwable ->
                    throwable.printStackTrace()
                    complete(StatusCodes.BAD_GATEWAY)
                }.build()
        )

    }
}