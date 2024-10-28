package com.commitAttack.web.swagger

import com.commitAttack.web.annotation.CADeleteMapping
import com.commitAttack.web.annotation.CAGetMapping
import com.commitAttack.web.annotation.CAPatchMapping
import com.commitAttack.web.annotation.CAPostMapping
import com.commitAttack.web.annotation.CAPutMapping
import com.commitAttack.web.dto.ApiFailResponseDto
import com.commitAttack.web.exception.ErrorTitle
import com.commitAttack.web.exception.annotation.CustomFailResponseAnnotation
import com.commitAttack.web.exception.annotation.CustomFailResponseAnnotations
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod

@Component
class CustomOperationCustomizer : OperationCustomizer {
    override fun customize(operation: Operation, handlerMethod: HandlerMethod): Operation {
        val methodAnnotations = handlerMethod.method.declaredAnnotations
        val responses = operation.responses
        if(methodAnnotations.find { it is Hidden } != null){
            return operation
        }
        for (annotation in methodAnnotations) {
            when (annotation) {
                is CustomFailResponseAnnotations -> {
                    for (j in annotation.value) {
                        val message = if (j.message == "") j.exception.message else j.message
                        handleCustomFailResponse(j.exception, message, responses)
                    }
                }
                is CustomFailResponseAnnotation -> {
                    val message = if (annotation.message == "") annotation.exception.message else annotation.message
                    handleCustomFailResponse(annotation.exception, message, responses)
                }
                is CAGetMapping -> {
                    if(annotation.authenticated || annotation.hasRole.isNotEmpty()){
                        operation.addSecurityItem(SecurityRequirement().addList("Bearer Authentication"))
                    }
                }
                is CAPostMapping -> {
                    if(annotation.authenticated || annotation.hasRole.isNotEmpty()){
                        operation.addSecurityItem(SecurityRequirement().addList("Bearer Authentication"))
                    }
                }
                is CAPatchMapping -> {
                    if(annotation.authenticated || annotation.hasRole.isNotEmpty()){
                        operation.addSecurityItem(SecurityRequirement().addList("Bearer Authentication"))
                    }
                }
                is CAPutMapping -> {
                    if(annotation.authenticated || annotation.hasRole.isNotEmpty()){
                        operation.addSecurityItem(SecurityRequirement().addList("Bearer Authentication"))
                    }
                }
                is CADeleteMapping -> {
                    if(annotation.authenticated || annotation.hasRole.isNotEmpty()){
                        operation.addSecurityItem(SecurityRequirement().addList("Bearer Authentication"))
                    }
                }
            }
        }

        operation.responses(responses)
        return operation
    }

    private fun handleCustomFailResponse(
        exception: ErrorTitle,
        message: String?,
        responses: ApiResponses
    ) {
        val statusCode = exception.status.value().toString()
        val response = responses.computeIfAbsent(statusCode) { ApiResponse() }
        val content = response.content ?: Content()
        val schema = Schema<Any>().`$ref`("#/components/schemas/ApiFailResponseDto")
        val errorResponse = ApiFailResponseDto(exception, message)
        val mediaType = content.getOrPut("application/json") { MediaType().schema(schema) }
        val example = Example().value(errorResponse)
        mediaType.addExamples(errorResponse.message, example)
        content["application/json"] = mediaType
        response.content(content)
        responses.addApiResponse(statusCode, response)
    }


}
