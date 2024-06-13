package weolbu.assignment.member.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import weolbu.assignment.common.constants.ValidConstants;
import weolbu.assignment.member.dto.SignupRequestDto;
import weolbu.assignment.member.entity.MemberRoleEnum;
import weolbu.assignment.validation.ValidationCustomUtils;

// SignupRequestDto 의 Validation 을 Test 합니다.
public class SignupRequestDtoValidationTest {
    private ValidationCustomUtils validationCustomUtils;
    SignupRequestDto signupRequestDto;

    @BeforeEach
    void setUp() {
        validationCustomUtils = new ValidationCustomUtils();
        signupRequestDto = SignupRequestDto.builder()
            .name("김학생")
            .email("student1@email.com")
            .mobileNumber("01012345678")
            .password("password1")
            .role(MemberRoleEnum.STUDENT)
            .build();
    }

    @Test
    @DisplayName("name - NotBlank")
    void nameBlank() {
        signupRequestDto.setName(null);
        assertEquals(ValidConstants.CANNOT_BE_BLANK, getValidationExMessage());
        signupRequestDto.setName("");
        assertEquals(ValidConstants.CANNOT_BE_BLANK, getValidationExMessage());
        signupRequestDto.setName(" ");
        assertEquals(ValidConstants.CANNOT_BE_BLANK, getValidationExMessage());
    }

    @Test
    @DisplayName("email - Pattern(xxx@ooo.zzz)")
    void emailPattern() {
        signupRequestDto.setEmail("student1@");
        assertEquals(ValidConstants.INVALID_EMAIL, getValidationExMessage());
        signupRequestDto.setEmail("@email");
        assertEquals(ValidConstants.INVALID_EMAIL, getValidationExMessage());
        signupRequestDto.setEmail("email.com");
        assertEquals(ValidConstants.INVALID_EMAIL, getValidationExMessage());
    }

    @Test
    @DisplayName("email - NotNull")
    void emailBlank() {
        signupRequestDto.setEmail(null);
        assertEquals(ValidConstants.CANNOT_BE_BLANK, getValidationExMessage());
    }

    @Test
    @DisplayName("mobileNumber - Pattern(숫자로만 이루어져 있다.)")
    void mobileNumberPattern() {
        signupRequestDto.setMobileNumber("01012345678r");
        assertEquals(ValidConstants.INVALID_MOBILE_NUMBER, getValidationExMessage());
    }

    @Test
    @DisplayName("mobileNumber - NotNull")
    void mobileNumberBlank() {
        signupRequestDto.setMobileNumber(null);
        assertEquals(ValidConstants.CANNOT_BE_BLANK, getValidationExMessage());
    }

    @Test
    @DisplayName("password - 6자 이상 10자 이하여야 한다.")
    void passwordLength() {
        signupRequestDto.setPassword("p1");
        assertEquals(ValidConstants.INVALID_PASSWORD_SIZE, getValidationExMessage());
        signupRequestDto.setPassword("abcde123456");
        assertEquals(ValidConstants.INVALID_PASSWORD_SIZE, getValidationExMessage());
    }

    @Test
    @DisplayName("password - 영소문자, 영대문자, 숫자 중 적어도 2개의 조합으로 이루어져야 한다.")
    void passwordPattern() {
        signupRequestDto.setPassword("aaaabbbb");
        assertEquals(ValidConstants.INVALID_PASSWORD, getValidationExMessage());
        signupRequestDto.setPassword("11112222");
        assertEquals(ValidConstants.INVALID_PASSWORD, getValidationExMessage());
        signupRequestDto.setPassword("AAAABBBB");
        assertEquals(ValidConstants.INVALID_PASSWORD, getValidationExMessage());
    }

    @Test
    @DisplayName("password - NotNull")
    void passwordBlank() {
        signupRequestDto.setPassword(null);
        assertEquals(ValidConstants.CANNOT_BE_BLANK, getValidationExMessage());
    }

    @Test
    @DisplayName("role - NotNull")
    void roleNull() {
        signupRequestDto.setRole(null);
        assertEquals(ValidConstants.CANNOT_BE_BLANK, getValidationExMessage());
    }

    private String getValidationExMessage() {
        return validationCustomUtils.getValidationExMessage(signupRequestDto);
    }
}
