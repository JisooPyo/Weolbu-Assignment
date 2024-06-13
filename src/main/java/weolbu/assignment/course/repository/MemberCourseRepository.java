package weolbu.assignment.course.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import weolbu.assignment.course.entity.MemberCourse;

@Repository
public interface MemberCourseRepository extends JpaRepository<MemberCourse, Integer> {
    @Query("SELECT mc FROM MemberCourse mc WHERE mc.member.id = :memberId AND mc.course.id = :courseId")
    Optional<MemberCourse> findByCourseIdAndMemberId(int courseId, int memberId);

    @Query("SELECT COUNT(mc) FROM MemberCourse mc WHERE mc.course.id = :courseId")
    int countByCourseId(int courseId);
}
