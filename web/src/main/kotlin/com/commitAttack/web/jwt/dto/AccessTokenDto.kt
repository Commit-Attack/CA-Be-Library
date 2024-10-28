package com.commitAttack.web.jwt.dto

import java.time.OffsetDateTime

data class AccessTokenDto(
    val accessToken: String,
    val expiredAt: OffsetDateTime
)
