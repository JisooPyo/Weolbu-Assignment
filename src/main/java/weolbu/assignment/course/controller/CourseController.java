package weolbu.assignment.course.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.course.dto.ApplyCourseRequestDto;
import weolbu.assignment.course.dto.CoursesResponseDto;
import weolbu.assignment.course.dto.CreateCourseRequestDto;
import weolbu.assignment.course.service.CourseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
@Tag(name = "Course API", description = "강의 관련 API 정보를 담고 있습니다.")
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "강의 개설 API")
    public ResponseEntity<ApiResponseDto> createCourse(@Valid @RequestBody CreateCourseRequestDto requestDto) {
        ApiResponseDto response = courseService.createCourse(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/applications")
    @Operation(summary = "강의 신청 API")
    public ResponseEntity<ApiResponseDto> applyCourse(@Valid @RequestBody ApplyCourseRequestDto requestDto) {
        ApiResponseDto response = courseService.applyCourse(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "강의 목록 조회 API")
    public ResponseEntity<CoursesResponseDto> getCourses(
        @RequestParam(defaultValue = "recent") String sortBy,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok().body(courseService.getCourses(sortBy, page - 1, size));
    }

    @GetMapping("/applications")
    @Operation(summary = "신청한 강의 목록 조회 API")
    public ResponseEntity<CoursesResponseDto> getAppliedCourses(
        @RequestBody Map<String, String> emailMap,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok().body(
            courseService.getAppliedCourses(emailMap.get("email"), page - 1, size)
        );
    }
}
