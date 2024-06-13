package weolbu.assignment.member.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import weolbu.assignment.course.entity.Course;
import weolbu.assignment.course.entity.MemberCourse;
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

    @OneToMany(mappedBy = "member")
    private List<Course> courses;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<MemberCourse> memberCourses;

    public SignupResponseDto toSignupResponseDto() {
        return SignupResponseDto.builder()
            .id(id)
            .name(name)
            .email(email)
            .mobileNumber(mobileNumber)
            .role(role)
            .build();
    }
}
