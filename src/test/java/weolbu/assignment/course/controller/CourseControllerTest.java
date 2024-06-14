package weolbu.assignment.course.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import weolbu.assignment.common.constants.CourseConstants;
import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.course.dto.ApplyCourseRequestDto;
import weolbu.assignment.course.dto.CoursesResponseDto;
import weolbu.assignment.course.dto.CreateCourseRequestDto;
import weolbu.assignment.course.service.CourseService;

@WebMvcTest(value = CourseController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class CourseControllerTest {

    @MockBean
    CourseService courseService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        reset(courseService);
    }

    @Test
    @DisplayName("강의 개설 API")
    void createCourse() throws Exception {
        // given
        CreateCourseRequestDto requestDto = CreateCourseRequestDto.builder()
            .name("강의제목")
            .email("teacher1@email.com")
            .price(200_000)
            .maxStudents(20)
            .build();
        ApiResponseDto apiResponseDto = new ApiResponseDto(CourseConstants.CREATE_COURSE_SUCCESS);
        given(courseService.createCourse(requestDto)).willReturn(apiResponseDto);

        // when
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("강의 신청 API")
    void applyCourse() throws Exception {
        // given
        ApplyCourseRequestDto requestDto = ApplyCourseRequestDto.builder()
            .email("student1@email.com")
            .courses(List.of(1))
            .build();

        ApiResponseDto apiResponseDto = new ApiResponseDto(CourseConstants.APPLY_COURSE_SUCCESS);
        given(courseService.applyCourse(requestDto)).willReturn(apiResponseDto);

        // when
        mockMvc.perform(post("/api/courses/application")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("강의 목록 조회 API")
    void getCourses() throws Exception {
        // given
        CoursesResponseDto coursesResponseDto = new CoursesResponseDto();
        given(courseService.getCourses(anyString(), anyInt(), anyInt())).willReturn(coursesResponseDto);

        // when
        mockMvc.perform(get("/api/courses")
                .param("sortBy", "recent")
                .param("page", "1")
                .param("size", "20"))
            .andExpect(status().isOk());
    }
}
