package weolbu.assignment.course.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AppliedCourseDto {
    private int id;
    private String name;
    private String message;
}
