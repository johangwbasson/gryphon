package net.johanbasson.gryphon.domain.users

import arrow.core.Either
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import net.johanbasson.gryphon.domain.ApiError
import java.util.*

class AuthorizeKtTest : FunSpec({
    val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    test("Should return a principal with a correct JWT token") {
        runBlocking {
            val id = UUID.randomUUID()
            val token = generateToken(User(id, "admin", ""), secretKey)
            val result = authorize("BEARER ${token.value}", secretKey)
            when (result) {
                is Either.Left -> fail("Should not return an error")
                is Either.Right -> result.b.id shouldBe id
            }
        }
    }

    test("Should return error when invalid jwt token specified") {
        val result = authorize("BEARER hjghghjsfgjgjsf", secretKey)
        when (result) {
            is Either.Left -> result.a shouldBe ApiError.InvalidAuthorizationHeader
            is Either.Right -> fail("Should not return authenticated user")
        }
    }

    test("Should return error when no jwt token specified") {
        val result = authorize("BEARER", secretKey)
        when (result) {
            is Either.Left -> result.a shouldBe ApiError.InvalidAuthorizationHeader
            is Either.Right -> fail("Should not return authenticated user")
        }
    }

    test("Should return error when no auth header is specified") {
        val result = authorize("", secretKey)
        when (result) {
            is Either.Left -> result.a shouldBe ApiError.InvalidAuthorizationHeader
            is Either.Right -> fail("Should not return authenticated user")
        }
    }
})
