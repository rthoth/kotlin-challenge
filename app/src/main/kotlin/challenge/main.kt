package challenge

import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.http.javadsl.Http
import challenge.module.RepositoryModule

fun main() {

    val repositoryModule = RepositoryModule()
    val system = ActorSystem.create<Unit>(Behaviors.empty(), "http-server")
    val http = Http.get(system)
    println("Woohoo!")
}
