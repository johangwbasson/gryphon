package net.johanbasson.gryphon.domain.users

import arrow.core.Either
import arrow.core.flatMap
import net.johanbasson.gryphon.domain.ApiError
import net.johanbasson.gryphon.domain.checkPermission
import org.mindrot.jbcrypt.BCrypt
import java.util.*

data class CreateUserRequest(val email: String, val password: String)

typealias CreateUserResponse = suspend (CreateUserRequest) -> Either<ApiError, User>

suspend fun createUser(authUser: AuthenticatedUser, getUserByEmail: GetUserByEmail, persistNewUser: PersistNewUser): CreateUserResponse = { request ->
    checkPermission(authUser, Role.ADMINISTRATOR).flatMap {
        when (getUserByEmail(request.email)) {
            is Either.Right -> Either.left(ApiError.UserAlreadyExists)
            is Either.Left -> persistNewUser(User(UUID.randomUUID(), request.email, BCrypt.hashpw(request.password, BCrypt.gensalt()), listOf(Role.USER)))
                                        .mapLeft { ApiError.DatabaseError }
        }
    }
}