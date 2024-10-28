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
        val controllers: Map<String, Any> = applicationContext.getBeansWithAnnotation(
            Controller::class.java
        )
        for (controller in controllers.values) {
            var parentPath: String? = null
            val methods = controller.javaClass.declaredMethods
            val requestMapping = controller.javaClass.getAnnotation(RequestMapping::class.java)
            if(requestMapping != null && requestMapping.value.isNotEmpty()){
                parentPath = requestMapping.value[0]
            }
            for (method in methods) {
                if (method.isAnnotationPresent(CAGetMapping::class.java)){
                    val CAGetMapping = method.getAnnotation(CAGetMapping::class.java)
                    val paths = CAGetMapping.value // 또는 requestMapping.path();
                    if(CAGetMapping.hasRole.isNotEmpty()) {
                        this.authorizeHttpRequests {
                            if (paths.isEmpty()){
                                it.requestMatchers(HttpMethod.GET, parentPath).hasAnyRole(*CAGetMapping.hasRole)
                            }
                            paths.forEach { p ->
                                var path = if(!p.startsWith("/")) "/$p" else p
                                path = if (parentPath == null || parentPath == "null") p else parentPath+path
                                it.requestMatchers(HttpMethod.GET, path).hasAnyRole(*CAGetMapping.hasRole)
                            }
                        }
                    }else if(CAGetMapping.authenticated){
                        this.authorizeHttpRequests {
                            if (paths.isEmpty()){
                                it.requestMatchers(HttpMethod.GET, parentPath).authenticated()
                            }
                            paths.forEach { p ->
                                var path = if(!p.startsWith("/")) "/$p" else p
                                path = if (parentPath == null || parentPath == "null") p else parentPath+path
                                it.requestMatchers(HttpMethod.GET, path).authenticated()
                            }
                        }
                    }
                }
                if (method.isAnnotationPresent(CAPostMapping::class.java)){
                    val CAPostMapping = method.getAnnotation(CAPostMapping::class.java)
                    val paths = CAPostMapping.value // 또는 requestMapping.path();
                    if(CAPostMapping.hasRole.isNotEmpty()) {
                        this.authorizeHttpRequests {
                            if(paths.isEmpty()){
                                it.requestMatchers(HttpMethod.POST, parentPath).hasAnyRole(*CAPostMapping.hasRole)
                            }
                            paths.forEach { p ->
                                var path = if(!p.startsWith("/")) "/$p" else p
                                path = if (parentPath == null || parentPath == "null") p else parentPath+path
                                it.requestMatchers(HttpMethod.POST, path).hasAnyRole(*CAPostMapping.hasRole)
                            }
                        }
                    }else if(CAPostMapping.authenticated){
                        this.authorizeHttpRequests {
                            if (paths.isEmpty()){
                                it.requestMatchers(HttpMethod.POST, parentPath).authenticated()
                            }
                            paths.forEach { p ->
                                var path = if(!p.startsWith("/")) "/$p" else p
                                path = if (parentPath == null || parentPath == "null") p else parentPath+path
                                it.requestMatchers(HttpMethod.POST, path).authenticated()
                            }
                        }
                    }
                }
                if (method.isAnnotationPresent(CAPatchMapping::class.java)){
                    val CAPatchMapping = method.getAnnotation(CAPatchMapping::class.java)
                    val paths = CAPatchMapping.value // 또는 requestMapping.path();
                    if(CAPatchMapping.hasRole.isNotEmpty()) {
                        this.authorizeHttpRequests {
                            if (paths.isEmpty()){
                                it.requestMatchers(HttpMethod.PATCH, parentPath).hasAnyRole(*CAPatchMapping.hasRole)
                            }
                            paths.forEach { p ->
                                var path = if(!p.startsWith("/")) "/$p" else p
                                path = if (parentPath == null || parentPath == "null") p else parentPath+path
                                it.requestMatchers(HttpMethod.PATCH, path).hasAnyRole(*CAPatchMapping.hasRole)
                            }
                        }
                    }else if(CAPatchMapping.authenticated){
                        this.authorizeHttpRequests {
                            if (paths.isEmpty()){
                                it.requestMatchers(HttpMethod.PATCH, parentPath).authenticated()
                            }
                            paths.forEach { p ->
                                var path = if(!p.startsWith("/")) "/$p" else p
                                path = if (parentPath == null || parentPath == "null") p else parentPath+path
                                it.requestMatchers(HttpMethod.PATCH, path).authenticated()
                            }
                        }
                    }
                }
                if (method.isAnnotationPresent(CAPutMapping::class.java)){
                    val CAPutMapping = method.getAnnotation(CAPutMapping::class.java)
                    val paths = CAPutMapping.value // 또는 requestMapping.path();
                    if(CAPutMapping.hasRole.isNotEmpty()) {
                        this.authorizeHttpRequests {
                            if (paths.isEmpty()){
                                it.requestMatchers(HttpMethod.PUT, parentPath).hasAnyRole(*CAPutMapping.hasRole)
                            }
                            paths.forEach { p ->
                                var path = if(!p.startsWith("/")) "/$p" else p
                                path = if (parentPath == null || parentPath == "null") p else parentPath+path
                                it.requestMatchers(HttpMethod.PUT, path).hasAnyRole(*CAPutMapping.hasRole)
                            }
                        }
                    }else if(CAPutMapping.authenticated){
                        this.authorizeHttpRequests {
                            if (paths.isEmpty()){
                                it.requestMatchers(HttpMethod.PUT, parentPath).authenticated()
                            }
                            paths.forEach { p ->
                                var path = if(!p.startsWith("/")) "/$p" else p
                                path = if (parentPath == null || parentPath == "null") p else parentPath+path
                                it.requestMatchers(HttpMethod.PUT, path).authenticated()
                            }
                        }
                    }
                }
                if (method.isAnnotationPresent(CADeleteMapping::class.java)){
                    val CADeleteMapping = method.getAnnotation(CADeleteMapping::class.java)
                    val paths = CADeleteMapping.value // 또는 requestMapping.path();
                    if(CADeleteMapping.hasRole.isNotEmpty()) {
                        this.authorizeHttpRequests {
                            if (paths.isEmpty()){
                                it.requestMatchers(HttpMethod.DELETE, parentPath).hasAnyRole(*CADeleteMapping.hasRole)
                            }
                            paths.forEach { p ->
                                var path = if(!p.startsWith("/")) "/$p" else p
                                path = if (parentPath == null || parentPath == "null") p else parentPath+path
                                it.requestMatchers(HttpMethod.DELETE, path).hasAnyRole(*CADeleteMapping.hasRole)
                            }
                        }
                    }else if(CADeleteMapping.authenticated){
                        this.authorizeHttpRequests {
                            if (paths.isEmpty()){
                                it.requestMatchers(HttpMethod.DELETE, parentPath).authenticated()
                            }
                            paths.forEach { p ->
                                var path = if(!p.startsWith("/")) "/$p" else p
                                path = if (parentPath == null || parentPath == "null") p else parentPath+path
                                it.requestMatchers(HttpMethod.DELETE, path).authenticated()
                            }
                        }
                    }
                }
            }
        }
        return this
    }
}