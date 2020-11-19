package net.johanbasson.gryphon.domain.users

import arrow.core.Either
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.johanbasson.gryphon.domain.ApiError
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class AuthenticateTests : FunSpec({

    val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    suspend fun getUserByEmail(email: String): Either<ApiError, User>  = Either.right(User(UUID.randomUUID(), "admin", BCrypt.hashpw("admin", BCrypt.gensalt())))

    suspend fun getUserByEmailNotFound(email: String): Either<ApiError, User> = Either.left(ApiError.UserNotFound)

    test("should authenticate with correct credentials") {
        runBlocking {
            val result: Either<ApiError, Token> = Authenticate(secretKey, ::getUserByEmail)(Credentials("admin", "admin"))
            result.fold(
                    { err -> fail(err.toString()) },
                    { token -> println(token) }
            )
        }
    }

    test("should return error if user could not be found") {
        runBlocking {
            val result: Either<ApiError, Token> = Authenticate(secretKey, ::getUserByEmailNotFound)(Credentials("admmin", "admin"))
            result.fold(
                    { err -> err shouldBe ApiError.InvalidEmailOrPassword },
                    { fail("No token should be generated if user does not exists") }
            )
        }
    }

    test("should return error if password is incorrect") {
        GlobalScope.launch {
            val result: Either<ApiError, Token> = Authenticate(secretKey, ::getUserByEmail)(Credentials("admin", "admin1"))
            result.fold(
                    { err -> err shouldBe ApiError.InvalidEmailOrPassword },
                    { token -> fail("No token should be generated") }
            )
        }.join()
    }
})