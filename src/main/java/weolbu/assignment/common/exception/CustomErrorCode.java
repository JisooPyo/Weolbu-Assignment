package weolbu.assignment.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import weolbu.assignment.common.constants.ErrorMsgConstants;

@Getter
public enum CustomErrorCode {
    MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, ErrorMsgConstants.MEMBER_ALREADY_EXISTS);

    private final HttpStatus statusCode;
    private final String message;

    CustomErrorCode(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
