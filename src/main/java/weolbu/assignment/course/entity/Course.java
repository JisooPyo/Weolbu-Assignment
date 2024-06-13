package weolbu.assignment.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true)
    private Integer maxStudents;

    @Column(nullable = false)
    private int price;

    public CreateCourseResponseDto toCreateCourseResponseDto() {
        return CreateCourseResponseDto.builder()
            .id(id)
            .instructor(member.getName())
            .name(name)
            .maxStudents(maxStudents)
            .price(price)
            .build();
    }
}
