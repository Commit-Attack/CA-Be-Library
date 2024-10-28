package com.commitAttack.web.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CursorPaginatedWithTotalCountResponseDto <T>(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "다음 호출 때 필요한 커서 값(content가 null 라면 해당 값도 null)")
    val lastCursor: UUID?,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "다음 페이지 존재 여부")
    val hasNext: Boolean,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "전체 수")
    val totalCount: Int = 0,
    var content: List<T>
) {
    companion object {
        fun <T> toResponseDto(lastCursor: UUID?, hasNext: Boolean, totalCount: Int, content: List<T>): CursorPaginatedWithTotalCountResponseDto<T> {
            return CursorPaginatedWithTotalCountResponseDto(
                lastCursor = lastCursor,
                hasNext = hasNext,
                totalCount = totalCount,
                content = content
            )
        }
    }
}