package weolbu.assignment.course.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import weolbu.assignment.course.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Optional<Course> findByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findByIdForUpdate(int id);

    @Query("SELECT c FROM Course c ORDER BY c.id DESC")
    Page<Course> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT c FROM Course c LEFT JOIN c.memberCourses mc GROUP BY c.id ORDER BY COUNT(mc.id) DESC")
    Page<Course> findAllByOrderByStudentCountDesc(Pageable pageable);

    @Query("SELECT c FROM Course c LEFT JOIN c.memberCourses mc GROUP BY c.id ORDER BY (COUNT(mc.id) * 1.0 / COALESCE(c.maxStudents, 1)) DESC")
    Page<Course> findAllByOrderByEnrollmentRateDesc(Pageable pageable);
}
