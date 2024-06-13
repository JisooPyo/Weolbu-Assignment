package weolbu.assignment.course.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import weolbu.assignment.common.constants.CourseConstants;
import weolbu.assignment.common.constants.ValidConstants;

@Getter
@Builder
public class ApplyCourseRequestDto {
    @NotBlank(message = ValidConstants.CANNOT_BE_BLANK)
    private String email;

    @NotNull
    @Size(min = 1, message = CourseConstants.COURSE_APPLICATION_AT_LEAST_ONE)
    private List<Integer> courses;
}
