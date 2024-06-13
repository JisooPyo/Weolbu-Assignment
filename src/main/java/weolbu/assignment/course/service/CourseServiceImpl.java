package weolbu.assignment.course.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import weolbu.assignment.common.constants.CourseConstants;
import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.common.exception.CustomErrorCode;
import weolbu.assignment.common.exception.CustomException;
import weolbu.assignment.course.dto.CreateCourseRequestDto;
import weolbu.assignment.course.entity.Course;
import weolbu.assignment.course.repository.CourseRepository;
import weolbu.assignment.member.entity.Member;
import weolbu.assignment.member.entity.MemberRoleEnum;
import weolbu.assignment.member.service.MemberServiceImpl;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final MemberServiceImpl memberService;

    @Override
    public ApiResponseDto createCourse(CreateCourseRequestDto requestDto) {
        Member member = checkMember(requestDto.getEmail());
        checkCourseName(requestDto.getName());
        Course course = requestDto.toEntity(member);
        Course savedCourse = courseRepository.save(course);
        return new ApiResponseDto(
            CourseConstants.CREATE_COURSE_SUCCESS,
            savedCourse.toCreateCourseResponseDto()
        );
    }

    // 동일한 이름의 강의가 있는지 체크합니다.
    private void checkCourseName(String name) {
        if (courseRepository.findByName(name).isPresent()) {
            throw new CustomException(CustomErrorCode.COURSE_NAME_ALREADY_EXISTS);
        }
    }

    // 인증되지 않았거나 학생이면 강의를 등록할 수 없습니다.
    private Member checkMember(String email) {
        Member member = memberService.findMemberByEmail(email);
        if (member == null) {
            throw new CustomException(CustomErrorCode.INVALID_ACCESS);
        }
        if (member.getRole().equals(MemberRoleEnum.STUDENT)) {
            throw new CustomException(CustomErrorCode.STUDENT_CANNOT_CREATE_COURSE);
        }
        return member;
    }
}
