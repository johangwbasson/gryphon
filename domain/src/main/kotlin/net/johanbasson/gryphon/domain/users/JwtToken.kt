package net.johanbasson.gryphon.domain.users

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import io.jsonwebtoken.Jwts
import net.johanbasson.gryphon.domain.ApiError
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey

fun generateToken(user: User, secretKey: SecretKey): Token {
    val expires = LocalDateTime.now().plusHours(2)
    val instant: Instant = expires.atZone(ZoneId.systemDefault()).toInstant()
    val jwt = Jwts.builder()
            .setId(user.id.toString())
            .setSubject("USER")
            .setExpiration(Date.from(instant))
            .signWith(secretKey)
            .compact()
    return Token(jwt, expires)
}

fun validateToken(token: String, secretKey: SecretKey): Either<ApiError, AuthenticatedUser> {
    return try {
        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
        Right(AuthenticatedUser(UUID.fromString(claims.body.id)))
    } catch (ex: Exception) {
        Left(ApiError.InvalidAuthorizationHeader)
    }
}