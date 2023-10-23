package challenge

import akka.actor.typed.ActorSystem
import akka.actor.typed.DispatcherSelector
import akka.actor.typed.javadsl.Behaviors
import akka.http.javadsl.Http
import challenge.http.BookingHandler
import challenge.http.HttpRouter

fun main() {

    val repositoryModule = RepositoryModule()
    val managerModule = ManagerModule(repositoryModule)

    val system = ActorSystem.create<Unit>(Behaviors.empty(), "http-server")
    val http = Http.get(system)
    val executor = system.dispatchers().lookup(DispatcherSelector.blocking())

    http.newServerAt("0.0.0.0", 8080).bind(
        HttpRouter(
            bookingHandler = BookingHandler.create(managerModule, executor)
        ).create()
    )

    println("Listening at 0.0.0.0:8080")
}
