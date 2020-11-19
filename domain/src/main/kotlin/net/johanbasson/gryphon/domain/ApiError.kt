package net.johanbasson.gryphon.domain

sealed class ApiError  {
    object InvalidEmailOrPassword : ApiError()
    object UserNotFound: ApiError()
    object InvalidAuthorizationHeader : ApiError()

}