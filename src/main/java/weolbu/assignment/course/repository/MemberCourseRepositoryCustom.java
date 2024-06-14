package weolbu.assignment.course.repository;

import java.util.Optional;

import weolbu.assignment.course.entity.MemberCourse;

public interface MemberCourseRepositoryCustom {
    Optional<MemberCourse> findByCourseIdAndMemberId(int courseId, int memberId);
    int countByCourseId(int courseId);
}
