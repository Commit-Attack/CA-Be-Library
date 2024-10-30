package com.commitAttack.web.security.config

import com.commitAttack.web.annotation.CADeleteMapping
import com.commitAttack.web.annotation.CAGetMapping
import com.commitAttack.web.annotation.CAPatchMapping
import com.commitAttack.web.annotation.CAPostMapping
import com.commitAttack.web.annotation.CAPutMapping
import com.commitAttack.web.jwt.service.JwtService
import com.commitAttack.web.security.filter.ExceptionFilter
import com.commitAttack.web.security.filter.JwtAuthenticationFilter
import com.commitAttack.web.security.handler.CustomAccessDeniedHandler
import com.commitAttack.web.security.handler.JwtAuthenticationEntryPointHandler
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.lang.reflect.Method

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ComponentScan(basePackages = ["com.commitAttack.web"])
class SecurityConfig(
    private val applicationContext: ApplicationContext,
    private val jwtService: JwtService,
) {

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager? {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity) = http
        .csrf { it.disable() }
        .cors { it.configurationSource(corsConfigurationSource()) }
        .headers { it.frameOptions { fo -> fo.sameOrigin() } }
        .applyDynamicUrlSecurity(applicationContext)
        .authorizeHttpRequests {
            it.anyRequest().permitAll()
        }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .exceptionHandling {
            it
                .authenticationEntryPoint(JwtAuthenticationEntryPointHandler())
                .accessDeniedHandler(CustomAccessDeniedHandler())
        }
        .addFilterBefore(JwtAuthenticationFilter(jwtService), BasicAuthenticationFilter::class.java)
        .addFilterBefore(ExceptionFilter(), JwtAuthenticationFilter::class.java)
        .build()!!

    @Bean
    @Throws(Exception::class)
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOriginPatterns = listOf("*")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.exposedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Throws(java.lang.Exception::class)
    fun HttpSecurity.applyDynamicUrlSecurity(applicationContext: ApplicationContext): HttpSecurity {
        val controllers: Map<String, Any> = applicationContext.getBeansWithAnnotation(Controller::class.java)

        controllers.values.forEach { controller ->
            val parentPath = controller.javaClass.getAnnotation(RequestMapping::class.java)
                ?.value?.firstOrNull()

            controller.javaClass.declaredMethods.forEach { method ->
                processMethodSecurity(method, parentPath)
            }
        }

        return this
    }

    private fun HttpSecurity.processMethodSecurity(method: Method, parentPath: String?) {
        when {
            method.isAnnotationPresent(CAGetMapping::class.java) ->
                processMapping(method.getAnnotation(CAGetMapping::class.java), HttpMethod.GET, parentPath)
            method.isAnnotationPresent(CAPostMapping::class.java) ->
                processMapping(method.getAnnotation(CAPostMapping::class.java), HttpMethod.POST, parentPath)
            method.isAnnotationPresent(CAPatchMapping::class.java) ->
                processMapping(method.getAnnotation(CAPatchMapping::class.java), HttpMethod.PATCH, parentPath)
            method.isAnnotationPresent(CAPutMapping::class.java) ->
                processMapping(method.getAnnotation(CAPutMapping::class.java), HttpMethod.PUT, parentPath)
            method.isAnnotationPresent(CADeleteMapping::class.java) ->
                processMapping(method.getAnnotation(CADeleteMapping::class.java), HttpMethod.DELETE, parentPath)
        }
    }

    private fun <T : Annotation> HttpSecurity.processMapping(
        annotation: T,
        httpMethod: HttpMethod,
        parentPath: String?
    ) {
        val mappingInfo = MappingInfo(
            value = when (annotation) {
                is CAGetMapping -> annotation.value
                is CAPostMapping -> annotation.value
                is CAPatchMapping -> annotation.value
                is CAPutMapping -> annotation.value
                is CADeleteMapping -> annotation.value
                else -> emptyArray()
            },
            hasRole = when (annotation) {
                is CAGetMapping -> annotation.hasRole
                is CAPostMapping -> annotation.hasRole
                is CAPatchMapping -> annotation.hasRole
                is CAPutMapping -> annotation.hasRole
                is CADeleteMapping -> annotation.hasRole
                else -> emptyArray()
            },
            authenticated = when (annotation) {
                is CAGetMapping -> annotation.authenticated
                is CAPostMapping -> annotation.authenticated
                is CAPatchMapping -> annotation.authenticated
                is CAPutMapping -> annotation.authenticated
                is CADeleteMapping -> annotation.authenticated
                else -> false
            },
            httpMethod = httpMethod
        )

        applySecurityRules(mappingInfo, parentPath)
    }

    private fun HttpSecurity.applySecurityRules(mappingInfo: MappingInfo, parentPath: String?) {
        when {
            mappingInfo.hasRole.isNotEmpty() -> applyRoleBasedSecurity(mappingInfo, parentPath)
            mappingInfo.authenticated -> applyAuthenticatedSecurity(mappingInfo, parentPath)
        }
    }

    private fun HttpSecurity.applyRoleBasedSecurity(
        mappingInfo: MappingInfo,
        parentPath: String?
    ) {
        authorizeHttpRequests {
            if (mappingInfo.value.isEmpty()) {
                it.requestMatchers(mappingInfo.httpMethod, parentPath)
                    .hasAnyRole(*mappingInfo.hasRole)
            }
            mappingInfo.value.forEach { path ->
                val processedPath = processPath(path, parentPath)
                it.requestMatchers(mappingInfo.httpMethod, processedPath)
                    .hasAnyRole(*mappingInfo.hasRole)
            }
        }
    }

    private fun HttpSecurity.applyAuthenticatedSecurity(
        mappingInfo: MappingInfo,
        parentPath: String?
    ) {
        authorizeHttpRequests {
            if (mappingInfo.value.isEmpty()) {
                it.requestMatchers(mappingInfo.httpMethod, parentPath).authenticated()
            }
            mappingInfo.value.forEach { path ->
                val processedPath = processPath(path, parentPath)
                it.requestMatchers(mappingInfo.httpMethod, processedPath).authenticated()
            }
        }
    }

    private fun processPath(path: String, parentPath: String?): String {
        val normalizedPath = if (!path.startsWith("/")) "/$path" else path
        return if (parentPath == null || parentPath == "null") path else parentPath + normalizedPath
    }

    data class MappingInfo(
        val value: Array<out String>,
        val hasRole: Array<out String>,
        val authenticated: Boolean,
        val httpMethod: HttpMethod
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MappingInfo

            if (!value.contentEquals(other.value)) return false
            if (!hasRole.contentEquals(other.hasRole)) return false
            if (authenticated != other.authenticated) return false
            if (httpMethod != other.httpMethod) return false

            return true
        }

        override fun hashCode(): Int {
            var result = value.contentHashCode()
            result = 31 * result + hasRole.contentHashCode()
            result = 31 * result + authenticated.hashCode()
            result = 31 * result + httpMethod.hashCode()
            return result
        }
    }
}