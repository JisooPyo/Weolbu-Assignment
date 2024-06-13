package weolbu.assignment.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import weolbu.assignment.member.dto.SignupResponseDto;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 20)
    private String mobileNumber;

    @Column(nullable = false, length = 20)
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private MemberRoleEnum role;

    public SignupResponseDto toSignupResponseDto() {
        return SignupResponseDto.builder()
            .id(this.id)
            .name(this.name)
            .email(this.email)
            .mobileNumber(this.mobileNumber)
            .role(this.role)
            .build();
    }
}
