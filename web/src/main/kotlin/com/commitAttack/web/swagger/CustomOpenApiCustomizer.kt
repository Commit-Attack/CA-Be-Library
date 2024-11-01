package com.commitAttack.web.swagger

import com.commitAttack.web.exception.ErrorTitle
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.stereotype.Component
import java.util.stream.Stream

@Component
class CustomOpenApiCustomizer : OpenApiCustomizer{
    override fun customise(openApi: OpenAPI?) {
        openApi?.components?.addSchemas("ApiFailResponseDto",
                Schema<Any>()
                        .name("ApiFailResponseDto")
                        .required(listOf("success", "message", "errorTitle"))
                        .type("object")
                        .properties(
                                mapOf(
                                        "success" to Schema<Any>().type("boolean").description("성공 여부"),
                                        "message" to Schema<Any>().type("string").description("응답 메세지"),
                                        "errorTitle" to Schema<Any>().type("string").description("에러 타이틀")._enum(Stream.of(*ErrorTitle.values()).map { it.getName() }.toList())
                                )
                        )
                        .description("에러 응답 DTO")
        )
    }
}