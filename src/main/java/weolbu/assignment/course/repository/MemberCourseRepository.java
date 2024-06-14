package weolbu.assignment.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import weolbu.assignment.course.entity.MemberCourse;

@Repository
public interface MemberCourseRepository extends JpaRepository<MemberCourse, Integer>, MemberCourseRepositoryCustom {

}
