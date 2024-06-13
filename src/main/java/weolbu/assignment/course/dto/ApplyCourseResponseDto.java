package weolbu.assignment.course.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyCourseResponseDto {
    private List<CourseResponseDto> success;
    private List<CourseResponseDto> failure;
}
