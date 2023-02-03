package com.rjeangilles.unwire.challenge.controller.exception

import com.rjeangilles.unwire.challenge.model.JourneyId
import com.rjeangilles.unwire.challenge.model.UserId

abstract class NotFoundException(message: String)
    : RuntimeException(message)

class JourneyNotFoundException(val id: JourneyId)
    : NotFoundException("Journey $id not found")


class UserNotFoundException(val id: UserId)
    : NotFoundException("User $id not found")

abstract class IdNotNullException(message: String)
    : RuntimeException(message)

class JourneyIdNotNullException(val id: JourneyId)
    : IdNotNullException("Journey id should be undefined, but found $id")
