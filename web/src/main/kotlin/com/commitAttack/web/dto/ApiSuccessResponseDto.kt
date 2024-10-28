package com.commitAttack.web.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "응답 DTO")
data class ApiSuccessResponseDto<T>(
        @Schema(description = "성공 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        val success: Boolean = true,

        @Schema(description = "응답 메세지", example = "string", requiredMode = Schema.RequiredMode.REQUIRED)
        val message: String,

        @Schema(description = "응답 바디", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonInclude(JsonInclude.Include.ALWAYS)
        val data: T?
) {
   constructor(message: String, data: T?) : this(true, message, data)
   constructor(message: String) : this(true, message, null)
}