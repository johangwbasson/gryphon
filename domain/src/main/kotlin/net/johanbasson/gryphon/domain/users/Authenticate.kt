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

}