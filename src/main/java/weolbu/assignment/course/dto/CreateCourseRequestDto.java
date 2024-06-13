package weolbu.assignment.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import weolbu.assignment.common.constants.ValidConstants;
import weolbu.assignment.course.entity.Course;
import weolbu.assignment.member.entity.Member;

@Getter
@Setter
@Builder
public class CreateCourseRequestDto {
    @NotNull(message = ValidConstants.CANNOT_BE_BLANK)
    private String email;

    @NotBlank(message = ValidConstants.CANNOT_BE_BLANK)
    private String name;

    @Min(value = 0, message = ValidConstants.MAX_STUDENTS_NON_NEGATIVE)
    private Integer maxStudents;

    @Min(value = 0, message = ValidConstants.PRICE_NON_NEGATIVE)
    private int price;

    public Course toEntity(Member member) {
        return Course.builder()
            .member(member)
            .name(this.name)
            .maxStudents(this.maxStudents)
            .price(this.price)
            .build();
    }
}
