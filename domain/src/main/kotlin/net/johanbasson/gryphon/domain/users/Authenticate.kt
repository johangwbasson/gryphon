package net.johanbasson.gryphon.domain.users

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import io.jsonwebtoken.Jwts
import net.johanbasson.gryphon.domain.ApiError
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey

data class Credentials(val email: String, val password: String)
data class Token(val value: String, val expires: LocalDateTime)

typealias TokenResponse = suspend (Credentials) -> Either<ApiError, Token>

object Authenticate {

    operator fun invoke(secretKey: SecretKey, getUserByEmail: GetUserByEmail): TokenResponse =  {credentials ->
       getUserByEmail(credentials.email)
               .mapLeft { ApiError.InvalidEmailOrPassword }
               .flatMap { user -> checkPassword(user, credentials)}
               .flatMap { user -> Either.right(generateToken(user,secretKey)) }
    }


    private fun checkPassword(user: User, credentials: Credentials): Either<ApiError, User> {
        return if (BCrypt.checkpw(credentials.password, user.hash)) {
            Right(user)
        } else {
            Left(ApiError.InvalidEmailOrPassword)
        }
    }

    private fun generateToken(user: User, secretKey: SecretKey): Token {
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

}