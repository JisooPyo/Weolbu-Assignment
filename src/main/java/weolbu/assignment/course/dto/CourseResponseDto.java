package weolbu.assignment.course.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseResponseDto {
    int id;
    String name;
    int price;
    String instructor;
    int applicants;
    Integer maxStudents;
}
