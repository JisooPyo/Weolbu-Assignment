package weolbu.assignment.course.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import weolbu.assignment.course.dto.ApplyCourseRequestDto;
import weolbu.assignment.course.entity.Course;
import weolbu.assignment.course.repository.CourseRepository;
import weolbu.assignment.course.repository.MemberCourseRepository;
import weolbu.assignment.course.service.CourseService;
import weolbu.assignment.member.entity.Member;
import weolbu.assignment.member.entity.MemberRoleEnum;
import weolbu.assignment.member.repository.MemberRepository;

@SpringBootTest
public class CourseApplicationConcurrencyTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCourseRepository memberCourseRepository;

    @Autowired
    private CourseRepository courseRepository;

    int courseId;
    Course course;

    @BeforeEach
    void setUp() {
        // 학생 20명 저장
        for (int i = 0; i < 20; i++) {
            String name = "학생" + i;
            String email = "student" + i + "@email.com";
            String password = "password" + i;
            String mobileNumber = "010123456" + i;
            memberRepository.save(Member.builder()
                .name(name)
                .email(email)
                .password(password)
                .mobileNumber(mobileNumber)
                .role(MemberRoleEnum.STUDENT).build());
        }
        // 강사 1명 저장
        memberRepository.save(Member.builder()
            .name("김선생")
            .email("kimteacher@email.com")
            .password("password1")
            .mobileNumber("01087654321")
            .role(MemberRoleEnum.INSTRUCTOR).build());

        Member instructor = memberRepository.findByEmail("kimteacher@email.com").orElse(null);

        // 강의 등록
        course = Course.builder()
            .member(instructor)
            .name("강의1")
            .maxStudents(5)
            .price(200_000)
            .build();
        courseRepository.save(course);

        Course course = courseRepository.findByName("강의1").orElse(null);
        courseId = course.getId();
    }

    @Test
    @DisplayName("강의 신청 - 동시성 제어 테스트")
    void applyCourseConcurrencyControlTest() throws InterruptedException {
        System.out.println("courseId = " + courseId);

        // given
        List<ApplyCourseRequestDto> requestDtos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            requestDtos.add(
                ApplyCourseRequestDto.builder()
                    .email("student" + i + "@email.com")
                    .courses(List.of(courseId))
                    .build()
            );
        }
        int numberOfThread = 20;

        // when
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThread);
        for (int i = 0; i < numberOfThread; i++) {
            ApplyCourseRequestDto requestDto = requestDtos.get(i);
            int threadNumber = i;
            Runnable task = () -> {
                System.out.println("Thread" + threadNumber + " started.");
                courseService.applyCourse(requestDto);
                System.out.println("Thread" + threadNumber + " finished.");
            };
            executor.submit(task);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // then
        int appliedCount = memberCourseRepository.countByCourseId(courseId);
        assertEquals(course.getMaxStudents(), appliedCount);
    }
}
