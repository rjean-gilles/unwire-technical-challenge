package com.rjeangilles.unwire.challenge.controller.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
internal class NotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFoundHandler(ex: NotFoundException): String {
        return ex.message ?: "Not found"
    }
}

@ControllerAdvice
internal class IdNotNullAdvice {
    @ResponseBody
    @ExceptionHandler(IdNotNullException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun idNotNullHandler(ex: IdNotNullException): String {
        return ex.message ?: "Id should be undefined"
    }
}