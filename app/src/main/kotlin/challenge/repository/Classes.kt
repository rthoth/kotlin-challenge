package challenge.repository

import kotlinx.coroutines.runBlocking

object Classes {

    val MobilePhone = "MobilePhone"
    val Member = "Member"
    val Booking = "Booking"

    fun prepare(session: Session): Session {
        runBlocking {
            session.attempt {
                it.createVertexClass(MobilePhone)
                it.createVertexClass(Member)
                it.createEdgeClass(Booking)
            }
        }

        return session
    }
}