package net.johanbasson.gryphon.domain.users

import com.google.gson.annotations.SerializedName
import java.util.*

enum class Role {
    @SerializedName("administrator")
    ADMINISTRATOR,
    @SerializedName("user")
    USER
}

data class User(val id: UUID, val email: String, val hash: String, val roles: List<Role>)