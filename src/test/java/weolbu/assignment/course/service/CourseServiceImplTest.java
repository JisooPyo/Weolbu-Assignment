package weolbu.assignment.course.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import weolbu.assignment.common.constants.CourseConstants;
import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.common.exception.CustomException;
import weolbu.assignment.course.dto.ApplyCourseRequestDto;
import weolbu.assignment.course.dto.ApplyCourseResponseDto;
import weolbu.assignment.course.dto.CoursesResponseDto;
import weolbu.assignment.course.dto.CreateCourseRequestDto;
import weolbu.assignment.course.dto.CreateCourseResponseDto;
import weolbu.assignment.course.entity.Course;
import weolbu.assignment.course.entity.MemberCourse;
import weolbu.assignment.course.repository.CourseRepository;
import weolbu.assignment.course.repository.MemberCourseRepository;
import weolbu.assignment.member.entity.Member;
import weolbu.assignment.member.entity.MemberRoleEnum;
import weolbu.assignment.member.service.MemberServiceImpl;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {
    @Mock
    CourseRepository courseRepository;
    @Mock
    MemberServiceImpl memberService;
    @Mock
    MemberCourseRepository memberCourseRepository;
    @InjectMocks
    CourseServiceImpl courseService;

    CreateCourseRequestDto createCourseRequestDto;
    ApplyCourseRequestDto applyCourseRequestDto;

    @BeforeEach
    void setUp() {
        createCourseRequestDto = CreateCourseRequestDto.builder()
            .name("강의제목")
            .email("teacher1@email.com")
            .price(200_000)
            .maxStudents(20)
            .build();
        applyCourseRequestDto = ApplyCourseRequestDto.builder()
            .email("student1@email.com")
            .courses(List.of(1))
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

    @Test
    @DisplayName("강의 신청 - 실패 : courseId에 맞는 강의가 존재하지 않을 경우")
    void applyCourseFailureByNotFoundCourse() {
        // given
        Member student = new Member();
        given(memberService.findMemberByEmail(anyString())).willReturn(student);
        given(courseRepository.findByIdForUpdate(anyInt())).willReturn(Optional.empty());

        // when
        assertThrows(CustomException.class, () -> courseService.applyCourse(applyCourseRequestDto));
    }

    @Test
    @DisplayName("강의 신청 - 실패 : 이미 신청한 강의인 경우")
    void applyCourseFailureByAlreadyApplied() {
        // given
        Member student = Member.builder().id(1).build();
        given(memberService.findMemberByEmail(anyString())).willReturn(student);
        Course course = Course.builder().id(1).name("강의1").build();
        given(courseRepository.findByIdForUpdate(anyInt())).willReturn(Optional.of(course));
        given(memberCourseRepository.findByCourseIdAndMemberId(anyInt(), anyInt()))
            .willReturn(Optional.of(new MemberCourse()));

        // when
        ApiResponseDto apiResponseDto = courseService.applyCourse(applyCourseRequestDto);

        // then
        ApplyCourseResponseDto responseDto = (ApplyCourseResponseDto)apiResponseDto.getData();
        assertEquals(1, responseDto.getFailure().size());
        assertEquals(CourseConstants.ALREADY_APPLIED, responseDto.getFailure().get(0).getMessage());
    }

    @Test
    @DisplayName("강의 신청 - 실패 : 신청한 강의가 최대 수강 인원에 도달한 경우")
    void applyCourseFailureByCourseFull() {
        // given
        Member student = Member.builder().id(1).build();
        given(memberService.findMemberByEmail(anyString())).willReturn(student);
        Course course = Course.builder().id(1).name("강의1").maxStudents(10).build();
        given(courseRepository.findByIdForUpdate(anyInt())).willReturn(Optional.of(course));
        given(memberCourseRepository.findByCourseIdAndMemberId(anyInt(), anyInt()))
            .willReturn(Optional.empty());
        given(memberCourseRepository.countByCourseId(anyInt())).willReturn(10);

        // when
        ApiResponseDto apiResponseDto = courseService.applyCourse(applyCourseRequestDto);

        // then
        ApplyCourseResponseDto responseDto = (ApplyCourseResponseDto)apiResponseDto.getData();
        assertEquals(1, responseDto.getFailure().size());
        assertEquals(CourseConstants.COURSE_FULL, responseDto.getFailure().get(0).getMessage());
    }

    @Test
    @DisplayName("강의 신청 - 성공")
    void applyCourseSuccess() {
        // given
        Member student = Member.builder().id(1).build();
        given(memberService.findMemberByEmail(anyString())).willReturn(student);
        Course course = Course.builder().id(1).name("강의1").maxStudents(10).build();
        given(courseRepository.findByIdForUpdate(anyInt())).willReturn(Optional.of(course));
        given(memberCourseRepository.findByCourseIdAndMemberId(anyInt(), anyInt()))
            .willReturn(Optional.empty());
        given(memberCourseRepository.countByCourseId(anyInt())).willReturn(8);

        // when
        ApiResponseDto apiResponseDto = courseService.applyCourse(applyCourseRequestDto);

        // then
        ApplyCourseResponseDto responseDto = (ApplyCourseResponseDto)apiResponseDto.getData();
        assertEquals(applyCourseRequestDto.getCourses().size(), responseDto.getSuccess().size());
    }

    @Test
    @DisplayName("강의 목록 조회 - 실패 : 잘못된 페이지 번호")
    void getCoursesFailureByInvalidPage() {
        // when
        assertThrows(CustomException.class, () -> courseService.getCourses("recent", -1, 20));
    }

    @Test
    @DisplayName("강의 목록 조회 - 실패 : 잘못된 페이지 크기")
    void getCoursesFailureByInvalidSize() {
        // when
        assertThrows(CustomException.class, () -> courseService.getCourses("recent", 0, 0));
    }

    @Test
    @DisplayName("강의 목록 조회 - 실패 : 잘못된 정렬 기준")
    void getCoursesFailureByInvalidSortBy() {
        // when
        assertThrows(CustomException.class, () -> courseService.getCourses("wrongSort", 1, 20));
    }

    @Test
    @DisplayName("강의 목록 조회 - 실패 : 데이터가 없는 경우")
    void getCoursesFailureByNoData() {
        // given
        given(courseRepository.findAllByOrderByCreatedAtDesc(any())).willReturn(Page.empty());

        // when
        assertThrows(CustomException.class, () -> courseService.getCourses("recent", 1, 20));
    }

    @Test
    @DisplayName("강의 목록 조회 - 성공")
    void getCoursesSuccess() {
        // given
        int pageNumber = 0;
        int pageSize = 20;
        List<Course> courses = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            courses.add(Course.builder()
                .id(i)
                .name("강의" + i)
                .member(Member.builder().name("강사").build())
                .maxStudents(10).build()
            );
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Course> coursePage = new PageImpl<>(courses, pageable, courses.size());

        given(courseRepository.findAllByOrderByCreatedAtDesc(any())).willReturn(coursePage);
        given(memberCourseRepository.countByCourseId(anyInt())).willReturn(0);

        // when
        CoursesResponseDto responseDto = courseService.getCourses("recent", pageNumber, pageSize);

        // then
        assertEquals(courses.size(), responseDto.getCourseResponseDtos().size());
        assertEquals(pageSize, responseDto.getSize());
        assertEquals(courses.size(), responseDto.getTotalElements());
        verify(courseRepository, times(1)).findAllByOrderByCreatedAtDesc(any());
        verify(memberCourseRepository, times(courses.size())).countByCourseId(anyInt());
    }

    // 실패 케이스는 강의 목록 조회 API와 동일
    @Test
    @DisplayName("신청한 강의 목록 조회 - 성공")
    void getAppliedCoursesSuccess() {
        // given
        Member foundMember = Member.builder().id(1).build();
        given(memberService.findMemberByEmail(anyString())).willReturn(foundMember);

        List<MemberCourse> memberCourses = new ArrayList<>();
        Member teacher = Member.builder().name("강사1").build();
        for (int i = 0; i < 5; i++) {
            memberCourses.add(
                MemberCourse.builder()
                    .member(foundMember)
                    .course(Course.builder().member(teacher).id(i + 1).build())
                    .build()
            );
        }
        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<MemberCourse> memberCoursesPage = new PageImpl<>(memberCourses, pageable, memberCourses.size());

        given(memberCourseRepository.findAllByMemberIdOrderByIdDesc(anyInt(), any()))
            .willReturn(memberCoursesPage);
        given(memberCourseRepository.countByCourseId(anyInt())).willReturn(5);

        // when
        CoursesResponseDto responseDto = courseService.getAppliedCourses("email", pageNumber, pageSize);

        // then
        assertEquals(memberCourses.size(), responseDto.getCourseResponseDtos().size());
        assertEquals(pageSize, responseDto.getSize());
        assertEquals(memberCourses.size(), responseDto.getTotalElements());
        verify(memberCourseRepository, times(memberCourses.size())).countByCourseId(anyInt());
    }
}
