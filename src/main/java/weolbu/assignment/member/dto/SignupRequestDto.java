package weolbu.assignment.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import weolbu.assignment.common.constants.ValidConstants;
import weolbu.assignment.member.entity.Member;
import weolbu.assignment.member.entity.MemberRoleEnum;

@Getter
@Setter
@Builder
public class SignupRequestDto {
    @NotBlank(message = ValidConstants.CANNOT_BE_BLANK)
    private String name;

    @Email(
        regexp = "^.+@.+\\..+$",
        message = ValidConstants.INVALID_EMAIL
    )
    @NotNull(message = ValidConstants.CANNOT_BE_BLANK)
    private String email;

    @Pattern(
        regexp = "^[0-9]+$",
        message = ValidConstants.INVALID_MOBILE_NUMBER
    )
    @NotNull(message = ValidConstants.CANNOT_BE_BLANK)
    private String mobileNumber;

    @Size(min = 6, max = 10, message = ValidConstants.INVALID_PASSWORD_SIZE)
    @Pattern(
        regexp = "^(?:(?=.*[a-z])(?=.*[A-Z])|(?=.*[a-z])(?=.*\\d)|(?=.*[A-Z])(?=.*\\d)).*$",
        message = ValidConstants.INVALID_PASSWORD
    )
    @NotNull(message = ValidConstants.CANNOT_BE_BLANK)
    private String password;

    @NotNull(message = ValidConstants.CANNOT_BE_BLANK)
    private MemberRoleEnum role;

    public Member toMember() {
        return Member.builder()
            .name(this.name)
            .email(this.email)
            .mobileNumber(this.mobileNumber)
            .password(this.password)
            .role(this.role)
            .build();
    }
}
