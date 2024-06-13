package weolbu.assignment.member.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import weolbu.assignment.common.constants.MemberConstants;
import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.member.dto.SignupRequestDto;
import weolbu.assignment.member.entity.MemberRoleEnum;
import weolbu.assignment.member.service.MemberService;

@WebMvcTest(value = MemberController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        reset(memberService);
    }

    @Test
    void signup() throws Exception {
        // given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .name("김학생")
            .email("student1@email.com")
            .mobileNumber("01012345678")
            .password("password1")
            .role(MemberRoleEnum.STUDENT)
            .build();
        ApiResponseDto apiResponseDto = new ApiResponseDto(MemberConstants.SIGNUP_SUCCESS);
        given(memberService.signup(signupRequestDto)).willReturn(apiResponseDto);

        // when
        mockMvc.perform(post("/api/members/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signupRequestDto)))
            .andExpect(status().isCreated());
    }
}
