package weolbu.assignment.course.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import weolbu.assignment.course.entity.MemberCourse;

@Repository
public interface MemberCourseRepository extends JpaRepository<MemberCourse, Integer>, MemberCourseRepositoryCustom {
    @Query("SELECT mc FROM MemberCourse mc WHERE mc.member.id = :memberId ORDER BY mc.id DESC")
    Page<MemberCourse> findAllByMemberIdOrderByIdDesc(int memberId, Pageable pageable);
}
