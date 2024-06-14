package weolbu.assignment.course.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoursesResponseDto {
    long totalElements;
    int totalPages;
    int size;
    List<CourseResponseDto> courseResponseDtos;
}
