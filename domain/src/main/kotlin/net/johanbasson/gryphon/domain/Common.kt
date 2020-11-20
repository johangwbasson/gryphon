package net.johanbasson.gryphon.domain

import arrow.core.Either
import net.johanbasson.gryphon.domain.users.AuthenticatedUser
import net.johanbasson.gryphon.domain.users.Role

suspend fun checkPermission(authUser: AuthenticatedUser, requiredRole: Role): Either<ApiError, AuthenticatedUser> {
    if (authUser.roles.contains(requiredRole)) {
        return Either.Right(authUser)
    }

    return Either.Left(ApiError.InsufficientPrivileges)
}