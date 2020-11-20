package net.johanbasson.gryphon.domain.users

import arrow.core.Either
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldNotBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import net.johanbasson.gryphon.domain.ApiError
import java.io.IOException
import java.util.*

class CreateUserKtTest : FunSpec({

    val authUser = AuthenticatedUser(UUID.randomUUID(), listOf(Role.ADMINISTRATOR))

    suspend fun persistUserNoError(user: User): Either<Throwable, User> = Either.Right(user)
    suspend fun persistUserError(user: User): Either<Throwable, User> = Either.left(IOException("No database connection"))
    suspend fun getExistingUser(email: String): Either<ApiError, User> = Either.Right(User(UUID.randomUUID(), "ann", "aaa", listOf(Role.USER)))
    suspend fun getNullUser(email: String): Either<ApiError, User> = Either.Left(ApiError.UserNotFound)


    test("Should persist new user - no error") {
        runBlocking {
            when (val result = createUser(authUser, ::getNullUser, ::persistUserNoError)(CreateUserRequest("john@abc.com", "password"))) {
                is Either.Right -> result.b shouldNotBe null
                is Either.Left -> fail("Error returned ")
            }
        }
    }

    test("Should persist new user - error") {
        runBlocking {
            when (val result = createUser(authUser, ::getNullUser, ::persistUserError)(CreateUserRequest("john@abc.com", "password"))) {
                is Either.Right -> fail("Error returned")
                is Either.Left -> result.a shouldBe ApiError.DatabaseError
            }
        }
    }

    test("Should create user when authenticated user is an administrator") {
        runBlocking {
            when (val result = createUser(authUser, ::getNullUser, ::persistUserNoError)(CreateUserRequest("john@abc.com", "password"))) {
                is Either.Right -> result.b shouldNotBe null
                is Either.Left -> fail("Error returned ")
            }
        }
    }

    test("Should not create user when authenticated user is an user") {
        val authUser = AuthenticatedUser(UUID.randomUUID(), listOf(Role.USER))
        runBlocking {
            when (val result = createUser(authUser, ::getNullUser, ::persistUserNoError)(CreateUserRequest("john@abc.com", "password"))) {
                is Either.Right ->  fail("Error returned ")
                is Either.Left -> result.a shouldBe ApiError.InsufficientPrivileges
            }
        }
    }

    test("Should create a user when not other user with the same email exists") {
        runBlocking {
            when (val result = createUser(authUser, ::getNullUser, ::persistUserNoError)(CreateUserRequest("john@abc.com", "password"))) {
                is Either.Right -> result.b shouldNotBe null
                is Either.Left -> fail("Error returned ")
            }
        }
    }

    test("Should not create a user when another user with the same email exists") {
        runBlocking {
            when (val result = createUser(authUser, ::getExistingUser, ::persistUserNoError)(CreateUserRequest("ann@abc.com", "password"))) {
                is Either.Right -> fail("Should have returned error for duplicate user")
                is Either.Left -> result.a shouldBe ApiError.UserAlreadyExists
            }
        }
    }
})
