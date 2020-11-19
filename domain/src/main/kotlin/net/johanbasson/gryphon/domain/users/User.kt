package net.johanbasson.gryphon.domain.users

import java.util.*

data class User(val id: UUID, val email: String, val hash: String)