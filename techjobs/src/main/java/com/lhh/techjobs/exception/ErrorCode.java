package com.lhh.techjobs.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_KEY(8888, "Invalid key", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTS(1001, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1003, "username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    WRONG_USERNAME_OR_PASSWORD(1005, "Wrong username or password", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1008, "Invalid token", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS(1009, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    PHONE_EXISTS(1010, "Số điện thoại đã tồn tại", HttpStatus.BAD_REQUEST),
    EMPLOYER_ACCOUNT_NOT_ACTIVE(1011, "Employer account is not active", HttpStatus.BAD_REQUEST),
    FILE_INVALID(1012, "File invalid", HttpStatus.BAD_REQUEST),
    ;


    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
