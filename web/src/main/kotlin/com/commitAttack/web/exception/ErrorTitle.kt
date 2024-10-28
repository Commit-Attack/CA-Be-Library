package com.commitAttack.web.exception

import org.springframework.http.HttpStatus


enum class ErrorTitle(var status: HttpStatus, var message: String) {
    ///400
    ExternalServerError(HttpStatus.BAD_REQUEST, "외부 서버와 통신 과정 중 에러가 발생했습니다."),
    InvalidInputValue(HttpStatus.BAD_REQUEST, "잘못된 Request 형식 입니다."),
    InvalidEnumValue(HttpStatus.BAD_REQUEST, "잘못된 Enum Value 입니다."),
    BadRequest(HttpStatus.BAD_REQUEST, "잘못된 요청 입니다."),
    ModelValidationFail(HttpStatus.BAD_REQUEST, "모델 유효성 검사에 실패했습니다."),
    JsonConvertFail(HttpStatus.BAD_REQUEST, "Json 변환에 실패했습니다."),
    InvalidJsonType(HttpStatus.BAD_REQUEST, "잘못된 Json 형식 입니다."),
    NotSupportedType(HttpStatus.BAD_REQUEST, "지원하지 않는 타입입니다."),
    InvalidQueryParameter(HttpStatus.BAD_REQUEST, "잘못된 Query Parameter 입니다."),

    ///401
    LoginRequired(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    InvalidToken(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    ExpiredToken(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    Unauthorized(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),

    ///403
    Forbidden(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    ///404
    // 405
    MethodNotAllowed(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메소드입니다."),
    ///409
    Conflict(HttpStatus.CONFLICT, "충돌이 발생했습니다."),
    // 429
    ///500
    InternalServerError(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),

    ;
    open fun getName(): String? {
        return this.name
    }
}