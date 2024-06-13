package weolbu.assignment.member.dto;

import lombok.Builder;
import lombok.Getter;
import weolbu.assignment.member.entity.MemberRoleEnum;

@Getter
@Builder
public class SignupResponseDto {
    private int id;
    private String name;
    private String email;
    private String mobileNumber;
    private MemberRoleEnum role;
}
