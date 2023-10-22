package challenge.repository

import challenge.Member

interface MemberRepository {

    suspend fun add(member: Member): Member

    companion object {

        fun create(session: Session): MemberRepository = object : MemberRepository {

            override suspend fun add(member: Member): Member {
                return session.attempt {
                    it.newVertex(Classes.Member)
                        .set("id", member.id)
                        .set("name", member.name)
                        .store()

                    member
                }
            }
        }
    }
}