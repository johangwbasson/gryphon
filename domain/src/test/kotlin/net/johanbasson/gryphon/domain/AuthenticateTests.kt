package net.johanbasson.gryphon.domain

import arrow.core.Either
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.johanbasson.gryphon.domain.users.Authenticate
import net.johanbasson.gryphon.domain.users.Credentials
import net.johanbasson.gryphon.domain.users.Token
import net.johanbasson.gryphon.domain.users.User
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class AuthenticateTests : FunSpec({

    val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun getUserByEmail(email: String): Either<ApiError, User>  = Either.right(User(UUID.randomUUID(), "admin", BCrypt.hashpw("admin", BCrypt.gensalt())))

    fun getUserByEmailNotFound(email: String): Either<ApiError, User> = Either.left(ApiError.UserNotFound)


    test("should authenticate with correct credentials") {
        GlobalScope.launch {
            val result: Either<ApiError, Token> = Authenticate(secretKey, ::getUserByEmail)(Credentials("admin", "admin"))
            result.fold(
                    { err -> fail(err.toString()) },
                    { token -> println(token) }
            )
        }
    }

    test("should return error if user could not be found") {
        GlobalScope.launch {
            val result: Either<ApiError, Token> = Authenticate(secretKey, ::getUserByEmailNotFound)(Credentials("admmin", "admin"))
            result.fold(
                    { err -> err shouldBe  ApiError.InvalidEmailOrPassword },
                    { fail("No token should be generated if user does not exists") }
            )
        }
    }
})