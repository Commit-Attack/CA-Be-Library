package com.commitAttack.web.util

import com.commitAttack.web.dto.ApiFailResponseDto
import com.commitAttack.web.dto.ApiSuccessResponseDto
import com.commitAttack.web.exception.ApiException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

object ResponseUtil {
    fun <T> successResponse(message: String, data: T): ResponseEntity<ApiSuccessResponseDto<T>> {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(
            ApiSuccessResponseDto(
                message,
                data
            )
        )
    }

    fun successResponse(message: String): ResponseEntity<ApiSuccessResponseDto<Unit>> {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(
            ApiSuccessResponseDto(
                message
            )
        )
    }

    fun errorResponse(e: ApiException): ResponseEntity<ApiFailResponseDto> {
        return ResponseEntity.status(e.errorTitle.status).contentType(MediaType.APPLICATION_JSON).body(
            ApiFailResponseDto(
                e
            )
        )
    }
}