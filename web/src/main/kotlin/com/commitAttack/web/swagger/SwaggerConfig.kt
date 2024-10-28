package com.commitAttack.web.swagger

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
class SwaggerConfig(
    val customOperationCustomizer: CustomOperationCustomizer,
    val customOpenApiCustomizer: CustomOpenApiCustomizer,
    val servers: ServersUrlsProperties
) {
    @Bean
    fun api(): GroupedOpenApi? {

        return GroupedOpenApi.builder()
            .group("API")
            .pathsToMatch("/**")
            .addOpenApiCustomizer(customOpenApiCustomizer)
            .addOperationCustomizer(customOperationCustomizer)
            .build()
    }

    @Bean
    fun config(): OpenAPI {
        val urls = if (servers.urls.isNullOrEmpty()) listOf("http://localhost:8080") else servers.urls!!
        return OpenAPI()
            .info(Info().title("API").description("API 명세서").version("v1"))
            .servers(urls.map { Server().url(it) })
    }
}