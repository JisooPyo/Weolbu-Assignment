package weolbu.assignment.course.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import weolbu.assignment.common.constants.CourseConstants;
import weolbu.assignment.common.dto.ApiResponseDto;
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
}
