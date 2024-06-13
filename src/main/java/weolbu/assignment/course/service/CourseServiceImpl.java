package weolbu.assignment.course.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import weolbu.assignment.common.constants.CourseConstants;
import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.common.exception.CustomErrorCode;
import weolbu.assignment.common.exception.CustomException;
import weolbu.assignment.course.dto.ApplyCourseRequestDto;
import weolbu.assignment.course.dto.ApplyCourseResponseDto;
import weolbu.assignment.course.dto.CourseResponseDto;
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
        Member member = findMemberByEmail(requestDto.getEmail());
        if (member.getRole().equals(MemberRoleEnum.STUDENT)) {
            throw new CustomException(CustomErrorCode.STUDENT_CANNOT_CREATE_COURSE);
        }
        checkCourseName(requestDto.getName());
        Course course = requestDto.toEntity(member);
        Course savedCourse = courseRepository.save(course);
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
        List<CourseResponseDto> successList = new ArrayList<>();
        List<CourseResponseDto> failureList = new ArrayList<>();
        for (int courseId : requestDto.getCourses()) {
            Course course = findCourseById(courseId);
            MemberCourse foundMemberCourse = findMemberCourseByCourseIdAndMemberId(courseId, member.getId());

            // 이미 신청한 강의인 경우 fail
            if (foundMemberCourse != null) {
                CourseResponseDto failure = CourseResponseDto.builder()
                    .id(courseId)
                    .name(course.getName())
                    .message(CourseConstants.ALREADY_APPLIED).build();
                failureList.add(failure);
                continue;
            }

            // 강의 최대 수강 인원을 넘겼을 때
            int count = memberCourseRepository.countByCourseId(courseId);
            if (course.getMaxStudents() != null && course.getMaxStudents() == count) {
                CourseResponseDto failure = CourseResponseDto.builder()
                    .id(courseId)
                    .name(course.getName())
                    .message(CourseConstants.COURSE_FULL).build();
                failureList.add(failure);
                continue;
            }

            // 강의 신청
            MemberCourse memberCourse = MemberCourse.builder().member(member).course(course).build();
            memberCourseRepository.save(memberCourse);
            CourseResponseDto success = CourseResponseDto.builder()
                .id(courseId)
                .name(course.getName()).build();
            successList.add(success);
        }

        ApplyCourseResponseDto responseDto = toApplyCourseResponseDto(successList, failureList);
        return new ApiResponseDto(CourseConstants.APPLY_COURSE_SUCCESS, responseDto);
    }

    private ApplyCourseResponseDto toApplyCourseResponseDto(
        List<CourseResponseDto> successList,
        List<CourseResponseDto> failureList) {
        ApplyCourseResponseDto responseDto = new ApplyCourseResponseDto();
        if (!successList.isEmpty()) {
            responseDto.setSuccess(successList);
        }
        if (!failureList.isEmpty()) {
            responseDto.setFailure(failureList);
        }
        return responseDto;
    }

    private MemberCourse findMemberCourseByCourseIdAndMemberId(int courseId, int memberId) {
        return memberCourseRepository.findByCourseIdAndMemberId(courseId, memberId).orElse(null);
    }

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

    // 인증되지 않았거나 학생이면 강의를 등록할 수 없습니다.
    private Member findMemberByEmail(String email) {
        Member member = memberService.findMemberByEmail(email);
        if (member == null) {
            throw new CustomException(CustomErrorCode.INVALID_ACCESS);
        }
        return member;
    }
}
