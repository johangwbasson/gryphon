package net.johanbasson.gryphon.domain

sealed class ApiError  {
    object InvalidEmailOrPassword : ApiError()
    object UserNotFound: ApiError()
    object InvalidAuthorizationHeader : ApiError()
    object UserAlreadyExists : ApiError()
    object InsufficientPrivileges : ApiError()
    object DatabaseError: ApiError()

}