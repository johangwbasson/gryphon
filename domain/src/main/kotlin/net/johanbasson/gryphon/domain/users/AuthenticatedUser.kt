package net.johanbasson.gryphon.domain.users

import java.util.*

data class AuthenticatedUser(val id: UUID, val roles: List<Role>)