package weolbu.assignment.course.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyCourseResponseDto {
    private List<AppliedCourseDto> success;
    private List<AppliedCourseDto> failure;
}
