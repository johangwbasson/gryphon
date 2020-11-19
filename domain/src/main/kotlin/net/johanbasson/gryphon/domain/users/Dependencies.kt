package net.johanbasson.gryphon.domain.users

import arrow.core.Either
import net.johanbasson.gryphon.domain.ApiError


typealias GetUserByEmail = suspend (String) -> Either<ApiError, User>