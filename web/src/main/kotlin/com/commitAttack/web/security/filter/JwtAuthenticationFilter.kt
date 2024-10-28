package com.commitAttack.web.security.filter

import com.commitAttack.web.exception.ApiException
import com.commitAttack.web.exception.ErrorTitle
import com.commitAttack.web.jwt.service.JwtService
import com.commitAttack.web.security.service.CAUserDetails
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            // 일반 토큰의 경우
            val token = getTokenFromRequestHeader(request)

            if (token != null && jwtService.isTokenValid(token)) {
                val authentication: Authentication? = getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            throw e
        } catch (ex: ApiException) {
            throw ex
        }
        filterChain.doFilter(request, response)
    }

    private fun getAuthentication(accessToken: String?): Authentication? {
        val claims = jwtService.getClaimsFromJwt(accessToken!!)
        val userName: String = claims["userName"] as String
        val userId: String = claims.subject
        val userRole: String? = claims["role"] as String?

        val userDetails = CAUserDetails(userId, userName, userRole, accessToken)
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    private fun getTokenFromRequestHeader(request: HttpServletRequest): String? {
        val authorization = request.getHeader("Authorization")
        val regex = Regex("^Bearer .*")
        return authorization?.let { if (regex.matches(it)) it.replace("^Bearer( )*".toRegex(), "") else null }
    }
}