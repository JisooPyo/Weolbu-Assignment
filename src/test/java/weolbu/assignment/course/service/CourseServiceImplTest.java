package weolbu.assignment.course.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.common.exception.CustomException;
import weolbu.assignment.course.dto.CreateCourseRequestDto;
import weolbu.assignment.course.dto.CreateCourseResponseDto;
import weolbu.assignment.course.entity.Course;
import weolbu.assignment.course.repository.CourseRepository;
import weolbu.assignment.member.entity.Member;
import weolbu.assignment.member.entity.MemberRoleEnum;
import weolbu.assignment.member.service.MemberServiceImpl;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {
    @Mock
    CourseRepository courseRepository;
    @Mock
    MemberServiceImpl memberService;
    @InjectMocks
    CourseServiceImpl courseService;

    CreateCourseRequestDto createCourseRequestDto;

    @BeforeEach
    void setUp() {
        createCourseRequestDto = CreateCourseRequestDto.builder()
            .name("강의제목")
            .email("teacher1@email.com")
            .price(200_000)
            .maxStudents(20)
            .build();
    }

    @Test
    @DisplayName("강의 개설 - 실패 : 가입된 계정이 아닌 경우")
    void createCourseFailureByNotAuthenticated() {
        // given
        given(memberService.findMemberByEmail(anyString())).willReturn(null);

        // when
        assertThrows(CustomException.class, () -> courseService.createCourse(createCourseRequestDto));
    }

    @Test
    @DisplayName("강의 개설 - 실패 : 계정이 학생인 경우")
    void createCourseFailureByNotAuthorized() {
        // given
        Member member = Member.builder().role(MemberRoleEnum.STUDENT).build();
        given(memberService.findMemberByEmail(anyString())).willReturn(member);

        // when
        assertThrows(CustomException.class, () -> courseService.createCourse(createCourseRequestDto));
    }

    @Test
    @DisplayName("강의 개설 - 실패 : 강의명이 중복인 경우")
    void createCourseFailureByDuplicateCourseName() {
        // given
        Member member = Member.builder().role(MemberRoleEnum.INSTRUCTOR).build();
        given(memberService.findMemberByEmail(anyString())).willReturn(member);
        given(courseRepository.findByName(anyString())).willReturn(Optional.of(new Course()));

        // when
        assertThrows(CustomException.class, () -> courseService.createCourse(createCourseRequestDto));
    }

    @Test
    @DisplayName("강의 개설 - 성공")
    void createCourseSuccess() {
        // given
        String instructorName = "강사명";
        Member member = Member.builder()
            .name(instructorName)
            .role(MemberRoleEnum.INSTRUCTOR)
            .build();
        given(memberService.findMemberByEmail(anyString())).willReturn(member);
        given(courseRepository.findByName(anyString())).willReturn(Optional.empty());
        Course course = createCourseRequestDto.toEntity(member);
        course.setId(1);
        given(courseRepository.save(any(Course.class))).willReturn(course);

        // when
        ApiResponseDto apiResponseDto = courseService.createCourse(createCourseRequestDto);

        // then
        then(courseRepository).should().save(any(Course.class));
        then(courseRepository).shouldHaveNoMoreInteractions();
        CreateCourseResponseDto responseDto = (CreateCourseResponseDto)apiResponseDto.getData();
        assertEquals(instructorName, responseDto.getInstructor());
        assertEquals(course.getId(), responseDto.getId());
        assertEquals(course.getName(), responseDto.getName());
        assertEquals(course.getMaxStudents(), responseDto.getMaxStudents());
        assertEquals(course.getPrice(), responseDto.getPrice());
    }
}
