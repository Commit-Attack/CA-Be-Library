package com.commitAttack.web.security.filter

import com.commitAttack.web.dto.ApiFailResponseDto
import com.commitAttack.web.exception.ApiException
import com.commitAttack.web.exception.ErrorTitle
import com.commitAttack.web.util.ObjectMapperUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets

@Component
class ExceptionFilter(
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: ApiException) {
            writeErrorResponse(response, e.errorTitle)
        } catch (e: Exception) {
            writeErrorResponse(response, ErrorTitle.InternalServerError)
        }
    }

    @Throws(Exception::class)
    private fun writeErrorResponse(response: HttpServletResponse, errorCode: ErrorTitle) {
        val apiResponse = ApiFailResponseDto(errorCode, errorCode.message)
        response.status = errorCode.status.value()
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(ObjectMapperUtil.writeValueAsString(apiResponse))
    }
}