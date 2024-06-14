package weolbu.assignment.course.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import weolbu.assignment.common.constants.CourseConstants;
import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.common.exception.CustomErrorCode;
import weolbu.assignment.common.exception.CustomException;
import weolbu.assignment.course.dto.AppliedCourseDto;
import weolbu.assignment.course.dto.ApplyCourseRequestDto;
import weolbu.assignment.course.dto.ApplyCourseResponseDto;
import weolbu.assignment.course.dto.CourseResponseDto;
import weolbu.assignment.course.dto.CoursesResponseDto;
import weolbu.assignment.course.dto.CreateCourseRequestDto;
import weolbu.assignment.course.entity.Course;
import weolbu.assignment.course.entity.MemberCourse;
import weolbu.assignment.course.repository.CourseRepository;
import weolbu.assignment.course.repository.MemberCourseRepository;
import weolbu.assignment.member.entity.Member;
import weolbu.assignment.member.entity.MemberRoleEnum;
import weolbu.assignment.member.service.MemberServiceImpl;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final MemberServiceImpl memberService;
    private final MemberCourseRepository memberCourseRepository;

    @Override
    public ApiResponseDto createCourse(CreateCourseRequestDto requestDto) {
        // 인증 과정
        Member member = findMemberByEmail(requestDto.getEmail());

        // 학생은 강의를 개설할 수 없습니다.
        if (member.getRole().equals(MemberRoleEnum.STUDENT)) {
            throw new CustomException(CustomErrorCode.STUDENT_CANNOT_CREATE_COURSE);
        }

        // 중복된 강의명이 있는지 확인합니다.
        checkCourseName(requestDto.getName());

        // 강의 개설 정보를 객체로 바꾸어 DB에 저장합니다.
        Course course = requestDto.toEntity(member);
        Course savedCourse = courseRepository.save(course);

        // 강의 개설 성공 메시지와 개설된 강의 정보를 반환합니다.
        return new ApiResponseDto(
            CourseConstants.CREATE_COURSE_SUCCESS,
            savedCourse.toCreateCourseResponseDto()
        );
    }

    @Override
    public ApiResponseDto applyCourse(ApplyCourseRequestDto requestDto) {
        // 인증 과정
        Member member = findMemberByEmail(requestDto.getEmail());

        // 요청 리스트로 들어온 courseId에 맞는 강의를 차례로 강의 신청합니다.
        List<AppliedCourseDto> successList = new ArrayList<>();
        List<AppliedCourseDto> failureList = new ArrayList<>();
        for (int courseId : requestDto.getCourses()) {
            Course course = findCourseById(courseId);
            MemberCourse foundMemberCourse = findMemberCourseByCourseIdAndMemberId(courseId, member.getId());

            // 이미 신청한 강의인 경우 신청할 수 없습니다.
            if (foundMemberCourse != null) {
                AppliedCourseDto failure = AppliedCourseDto.builder()
                    .id(courseId)
                    .name(course.getName())
                    .message(CourseConstants.ALREADY_APPLIED).build();
                failureList.add(failure);
                continue;
            }

            // 강의 최대 수강 인원을 넘겼을 때
            int count = memberCourseRepository.countByCourseId(courseId);
            if (course.getMaxStudents() != null && course.getMaxStudents() == count) {
                AppliedCourseDto failure = AppliedCourseDto.builder()
                    .id(courseId)
                    .name(course.getName())
                    .message(CourseConstants.COURSE_FULL).build();
                failureList.add(failure);
                continue;
            }

            // 강의 신청
            MemberCourse memberCourse = MemberCourse.builder().member(member).course(course).build();
            memberCourseRepository.save(memberCourse);
            AppliedCourseDto success = AppliedCourseDto.builder()
                .id(courseId)
                .name(course.getName()).build();
            successList.add(success);
        }

        ApplyCourseResponseDto responseDto = toApplyCourseResponseDto(successList, failureList);

        // 강의 신청 성공 메시지와 강의 신청 정보를 반환합니다.
        if (failureList.size() == requestDto.getCourses().size()) {
            return new ApiResponseDto(CourseConstants.APPLY_COURSE_FAILURE, responseDto);
        } else if (!failureList.isEmpty()) {
            return new ApiResponseDto(CourseConstants.APPLY_COURSE_PARTIAL_SUCCESS, responseDto);
        } else {
            return new ApiResponseDto(CourseConstants.APPLY_COURSE_SUCCESS, responseDto);
        }
    }

    @Override
    public CoursesResponseDto getCourses(String sortBy, int page, int size) {
        // 페이지 번호, 페이지 크기 validation
        if (page < 0) {
            throw new CustomException(CustomErrorCode.INVALID_PAGE);
        }
        if (size <= 0) {
            throw new CustomException(CustomErrorCode.INVALID_SIZE);
        }

        // 요청된 page 번호와 size 에 맞는 Pageable 객체를 생성합니다.
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses;
        switch (sortBy) {
            case "recent":
                courses = courseRepository.findAllByOrderByCreatedAtDesc(pageable);
                break;
            case "studentCount":
                courses = courseRepository.findAllByOrderByStudentCountDesc(pageable);
                break;
            case "enrollmentRate":
                courses = courseRepository.findAllByOrderByEnrollmentRateDesc(pageable);
                break;
            default:
                throw new CustomException(CustomErrorCode.INVALID_SORTBY);
        }

        // 데이터가 없는 경우
        if (courses.isEmpty()) {
            throw new CustomException(CustomErrorCode.NO_MORE_DATA);
        }

        // Course -> CourseResponseDto 로 변경합니다.
        List<CourseResponseDto> courseResponseDtos = courses.get()
            .map(
                course -> {
                    int applicants = memberCourseRepository.countByCourseId(course.getId());
                    return course.toCourseResponseDto(applicants);
                }
            )
            .toList();

        // 페이징 정보, 강의 정보를 반환합니다.
        return CoursesResponseDto.builder()
            .totalElements(courses.getTotalElements())
            .totalPages(courses.getTotalPages())
            .size(courses.getSize())
            .courseResponseDtos(courseResponseDtos)
            .build();
    }

    // 강의 신청 성공 정보와 강의 신청 실패 정보를 병합하여 반환합니다.
    private ApplyCourseResponseDto toApplyCourseResponseDto(
        List<AppliedCourseDto> successList,
        List<AppliedCourseDto> failureList) {
        ApplyCourseResponseDto responseDto = new ApplyCourseResponseDto();
        if (!successList.isEmpty()) {
            responseDto.setSuccess(successList);
        }
        if (!failureList.isEmpty()) {
            responseDto.setFailure(failureList);
        }
        return responseDto;
    }

    // 강의 id 와 멤버 id에 맞는 강의 신청 정보를 불러옵니다.
    private MemberCourse findMemberCourseByCourseIdAndMemberId(int courseId, int memberId) {
        return memberCourseRepository.findByCourseIdAndMemberId(courseId, memberId).orElse(null);
    }

    // 강의 id 에 맞는 강의 객체를 불러옵니다.
    private Course findCourseById(int courseId) {
        return courseRepository.findByIdForUpdate(courseId).orElseThrow(
            () -> new CustomException(CustomErrorCode.COURSE_NOT_FOUND)
        );
    }

    // 동일한 이름의 강의가 있는지 체크합니다.
    private void checkCourseName(String name) {
        if (courseRepository.findByName(name).isPresent()) {
            throw new CustomException(CustomErrorCode.COURSE_NAME_ALREADY_EXISTS);
        }
    }

    // email 정보를 받아 Member 객체를 찾고 존재하지 않으면 예외를 던집니다.
    private Member findMemberByEmail(String email) {
        Member member = memberService.findMemberByEmail(email);
        if (member == null) {
            throw new CustomException(CustomErrorCode.INVALID_ACCESS);
        }
        return member;
    }
}
