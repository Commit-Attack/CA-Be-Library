package com.commitAttack.web.util

import com.commitAttack.web.exception.ApiException
import com.fasterxml.jackson.databind.ObjectMapper
import com.commitAttack.web.exception.ErrorTitle

object ObjectMapperUtil {
    fun writeValueAsString(data: Any): String {
        return ObjectMapper().writeValueAsString(data)
    }
    fun validateJsonString(json: String) {
        try {
            val mapper = ObjectMapper()
            mapper.readTree(json)
        } catch (e: Exception) {
            throw ApiException(ErrorTitle.InvalidJsonType)
        }
    }
}