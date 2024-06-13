package weolbu.assignment.course.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.course.dto.CreateCourseRequestDto;
import weolbu.assignment.course.service.CourseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponseDto> createCourse(@Valid @RequestBody CreateCourseRequestDto requestDto) {
        ApiResponseDto response = courseService.createCourse(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
