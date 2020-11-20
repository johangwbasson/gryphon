package net.johanbasson.gryphon.domain.users

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.jsonwebtoken.Jwts
import net.johanbasson.gryphon.domain.ApiError
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey

fun generateToken(user: User, secretKey: SecretKey): Token {
    val gson = Gson()
    val expires = LocalDateTime.now().plusHours(2)
    val instant: Instant = expires.atZone(ZoneId.systemDefault()).toInstant()
    val jwt = Jwts.builder()
            .setId(user.id.toString())
            .setSubject(gson.toJson(user.roles))
            .setExpiration(Date.from(instant))
            .signWith(secretKey)
            .compact()
    return Token(jwt, expires)
}

fun validateToken(token: String, secretKey: SecretKey): Either<ApiError, AuthenticatedUser> {
    val type = object : TypeToken<List<Role>>() {}.type
    return try {
        val gson = Gson()
        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
        Right(AuthenticatedUser(UUID.fromString(claims.body.id), gson.fromJson(claims.body.subject, type)))
    } catch (ex: Exception) {
        Left(ApiError.InvalidAuthorizationHeader)
    }
}