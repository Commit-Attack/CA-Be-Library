package com.commitAttack.web.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "커서 페이징 Request Dto")
data class CursorRequestDto(
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "커서 (null로 보낸다면 첫페이지)")
    val cursor: UUID?,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "페이지 사이즈", defaultValue = "10")
    val size: Int = 10
)
