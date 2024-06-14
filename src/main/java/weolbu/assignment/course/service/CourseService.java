package weolbu.assignment.course.service;

import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.course.dto.ApplyCourseRequestDto;
import weolbu.assignment.course.dto.CoursesResponseDto;
import weolbu.assignment.course.dto.CreateCourseRequestDto;

public interface CourseService {
    /**
     * 강의 개설에 필요한 정보를 받아 강의를 등록합니다.
     *
     * @param requestDto 강의 개설에 필요한 정보
     * @return 강의 개설 성공 메시지와 개설된 강의 정보
     */
    ApiResponseDto createCourse(CreateCourseRequestDto requestDto);

    /**
     * 강의 신청에 필요한 정보를 받아 강의를 신청합니다.
     *
     * @param requestDto 강의 신청에 필요한 정보
     * @return 강의 신청 성공 메시지와 신청한 강의 정보
     */
    ApiResponseDto applyCourse(ApplyCourseRequestDto requestDto);

    /**
     * 페이징에 필요한 정보를 받아 정렬된 강의 목록을 반환합니다.
     *
     * @param sortBy 강의를 정렬 기준
     * @param page 조회할 페이지 번호
     * @param size 페이지당 데이터 수
     * @return 페이징 정보와 강의 데이터 목록
     */
    CoursesResponseDto getCourses(String sortBy, int page, int size);

    /**
     * 페이징에 필요한 정보를 받아 내가 신청한 강의 목록을 정렬하여 반환합니다.
     *
     * @param email 인증정보
     * @param page 조회할 페이지 번호
     * @param size 페이지당 데이터 수
     * @return 페이징 정보와 강의 데이터 목록
     */
    CoursesResponseDto getAppliedCourses(String email, int page, int size);
}
