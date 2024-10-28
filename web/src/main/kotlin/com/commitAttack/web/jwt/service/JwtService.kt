package com.commitAttack.web.jwt.service

import com.commitAttack.web.exception.ApiException
import com.commitAttack.web.exception.ErrorTitle
import com.commitAttack.web.jwt.dto.AccessTokenDto
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.time.OffsetDateTime
import java.util.*

@Service
class JwtService(
    @Value("\${jwt.common.key}")
    private val jwtCommonKey: String,
){
    val key: Key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtCommonKey))
    }

    fun createJwt(subject: String, customClaims: Claims? = null, audience: String? = null): String {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer("CA")
                .setAudience(audience)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .addClaims(customClaims ?: mapOf())
                .compact()
    }

    fun generateToken(expiresAt: OffsetDateTime?, claims: Map<String, Any>, audience: String? = null): AccessTokenDto {
        val now = OffsetDateTime.now()
        val expires = expiresAt ?: now.plusHours(1)
        val tokenIssuer = "CA"
        val jwt = Jwts.builder()
            .setIssuer(tokenIssuer)
            .setAudience(audience)
            .setExpiration(Date.from(expires.toInstant()))
            .addClaims(claims)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()

        return AccessTokenDto(jwt, expires)
    }


    fun getClaimsFromJwt(token: String): Claims = try {
        Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    } catch (e: ExpiredJwtException) {
        throw ApiException(ErrorTitle.ExpiredToken)
    } catch (e: Exception) {
        throw ApiException(ErrorTitle.InvalidToken)
    }

    fun isTokenValid(token: String): Boolean {
        try {
            getClaimsFromJwt(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }
}