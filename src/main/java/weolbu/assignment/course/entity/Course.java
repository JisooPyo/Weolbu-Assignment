package weolbu.assignment.course.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import weolbu.assignment.course.dto.CourseResponseDto;
import weolbu.assignment.course.dto.CreateCourseResponseDto;
import weolbu.assignment.member.entity.Member;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true)
    private Integer maxStudents;

    @Column(nullable = false)
    private int price;

    @OneToMany(mappedBy = "course")
    private List<MemberCourse> memberCourses;

    public CreateCourseResponseDto toCreateCourseResponseDto() {
        return CreateCourseResponseDto.builder()
            .id(id)
            .instructor(member.getName())
            .name(name)
            .maxStudents(maxStudents)
            .price(price)
            .build();
    }

    public CourseResponseDto toCourseResponseDto(int applicants) {
        return CourseResponseDto.builder()
            .id(id)
            .name(name)
            .price(price)
            .instructor(member.getName())
            .applicants(applicants)
            .maxStudents(maxStudents)
            .build();
    }
}
