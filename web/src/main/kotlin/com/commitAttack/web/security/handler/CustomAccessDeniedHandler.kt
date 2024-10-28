package com.commitAttack.web.security.handler

import com.commitAttack.web.dto.ApiFailResponseDto
import com.commitAttack.web.exception.ApiException
import com.commitAttack.web.exception.ErrorTitle
import com.commitAttack.web.util.ObjectMapperUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler(
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?
    ) {
        val apiResponse = ApiFailResponseDto(ApiException(ErrorTitle.Forbidden))
        val responseBody = ObjectMapperUtil.writeValueAsString(apiResponse)

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpStatus.FORBIDDEN.value()
        response.characterEncoding = "UTF-8"
        response.writer.write(responseBody)
    }
}