package weolbu.assignment.course.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateCourseResponseDto {
    private int id;
    private String instructor;
    private String name;
    private Integer maxStudents;
    private int price;
}
