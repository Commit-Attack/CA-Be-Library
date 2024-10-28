package com.commitAttack.web.swagger

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "swagger-servers")
data class ServersUrlsProperties(
    var urls: List<String>? = null
) {
}