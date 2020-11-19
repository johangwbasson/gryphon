package net.johanbasson.gryphon.domain.users

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import io.jsonwebtoken.Jwts
import net.johanbasson.gryphon.domain.ApiError
import java.util.*
import javax.crypto.SecretKey

suspend fun authorize(header: String?, secretKey: SecretKey): Either<ApiError, AuthenticatedUser> {
    try {
        if (header == null || header.length < 7) {
            return Left(ApiError.InvalidAuthorizationHeader)
        }
        val token = header.substring(7).trim()
        return validateToken(token, secretKey)
    } catch (ex: Exception) {
        return Left(ApiError.InvalidAuthorizationHeader)
    }
}