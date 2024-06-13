package weolbu.assignment.course.service;

import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.course.dto.CreateCourseRequestDto;

public interface CourseService {
    /**
     * 강의 개설에 필요한 정보를 받아 강의를 등록합니다.
     *
     * @param requestDto 강의 개설에 필요한 정보
     * @return 강의 개설 성공 메시지와 개설된 강의 정보
     */
    ApiResponseDto createCourse(CreateCourseRequestDto requestDto);
}
