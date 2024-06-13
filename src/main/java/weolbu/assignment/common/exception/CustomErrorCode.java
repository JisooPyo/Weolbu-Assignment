package weolbu.assignment.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import weolbu.assignment.common.constants.ErrorMsgConstants;

@Getter
public enum CustomErrorCode {
    MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, ErrorMsgConstants.MEMBER_ALREADY_EXISTS),
    INVALID_ACCESS(HttpStatus.BAD_REQUEST, ErrorMsgConstants.INVALID_ACCESS),
    STUDENT_CANNOT_CREATE_COURSE(HttpStatus.UNAUTHORIZED, ErrorMsgConstants.STUDENT_CANNOT_CREATE_COURSE),
    COURSE_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, ErrorMsgConstants.COURSE_NAME_ALREADY_EXISTS);

    private final HttpStatus statusCode;
    private final String message;

    CustomErrorCode(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
