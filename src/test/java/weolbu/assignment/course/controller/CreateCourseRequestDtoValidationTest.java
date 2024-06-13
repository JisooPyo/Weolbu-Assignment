package weolbu.assignment.course.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import weolbu.assignment.common.constants.ValidConstants;
import weolbu.assignment.course.dto.CreateCourseRequestDto;
import weolbu.assignment.validation.ValidationCustomUtils;

// CreateCourseRequestDto 의 Validation 을 Test합니다.
class CreateCourseRequestDtoValidationTest {
    private ValidationCustomUtils validationCustomUtils;
    CreateCourseRequestDto createCourseRequestDto;

    @BeforeEach
    void setUp() {
        validationCustomUtils = new ValidationCustomUtils();
        createCourseRequestDto = CreateCourseRequestDto.builder()
            .name("강의 제목이 들어갑니다")
            .email("강사 email이 들어갑니다.")
            .price(200_000)
            .maxStudents(20)
            .build();
    }

    @Test
    @DisplayName("email - NotNull")
    void emailNull() {
        createCourseRequestDto.setEmail(null);
        assertEquals(ValidConstants.CANNOT_BE_BLANK, getValidationExMessage());
    }

    @Test
    @DisplayName("name - NotNull")
    void nameNull() {
        createCourseRequestDto.setName(null);
        assertEquals(ValidConstants.CANNOT_BE_BLANK, getValidationExMessage());
    }

    @Test
    @DisplayName("maxStudents - 0 이상이어야 한다.")
    void maxStudentsNegative() {
        createCourseRequestDto.setMaxStudents(-1);
        assertEquals(ValidConstants.MAX_STUDENTS_NON_NEGATIVE, getValidationExMessage());
    }

    @Test
    @DisplayName("price - 0 이상이어야 한다.")
    void priceNegative() {
        createCourseRequestDto.setPrice(-1);
        assertEquals(ValidConstants.PRICE_NON_NEGATIVE, getValidationExMessage());
    }

    private String getValidationExMessage() {
        return validationCustomUtils.getValidationExMessage(createCourseRequestDto);
    }
}
