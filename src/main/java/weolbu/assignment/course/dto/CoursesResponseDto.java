package weolbu.assignment.course.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursesResponseDto {
    long totalElements;
    int totalPages;
    int size;
    List<CourseResponseDto> courseResponseDtos;
}
