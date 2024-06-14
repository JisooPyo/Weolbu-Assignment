package weolbu.assignment.course.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import weolbu.assignment.course.entity.MemberCourse;
import weolbu.assignment.course.entity.QMemberCourse;

@Repository
@RequiredArgsConstructor
public class MemberCourseRepositoryImpl implements MemberCourseRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QMemberCourse memberCourse = QMemberCourse.memberCourse;

    @Override
    public Optional<MemberCourse> findByCourseIdAndMemberId(int courseId, int memberId) {

        MemberCourse result = jpaQueryFactory
            .selectFrom(memberCourse)
            .where(memberCourse.course.id.eq(courseId)
                .and(memberCourse.member.id.eq(memberId)))
            .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public int countByCourseId(int courseId) {
        Long count = jpaQueryFactory.select(memberCourse.count())
            .from(memberCourse)
            .where(memberCourse.course.id.eq(courseId))
            .fetchOne();

        return count == null ? 0 : (int)(long)count;
    }
}
