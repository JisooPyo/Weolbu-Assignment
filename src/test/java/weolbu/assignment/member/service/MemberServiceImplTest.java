package weolbu.assignment.member.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.common.exception.CustomException;
import weolbu.assignment.member.dto.SignupRequestDto;
import weolbu.assignment.member.dto.SignupResponseDto;
import weolbu.assignment.member.entity.Member;
import weolbu.assignment.member.entity.MemberRoleEnum;
import weolbu.assignment.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    MemberServiceImpl memberService;
    SignupRequestDto signupRequestDto;

    @BeforeEach
    void setUp() {
        signupRequestDto = SignupRequestDto.builder()
            .name("김학생")
            .email("student1@email.com")
            .mobileNumber("01012345678")
            .password("password1")
            .role(MemberRoleEnum.STUDENT)
            .build();
    }

    @Test
    @DisplayName("회원가입 - 실패 : 이미 가입된 계정인 경우")
    void signupFailureByExistingAccount() {
        // given
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(new Member()));

        assertThrows(CustomException.class, () -> memberService.signup(signupRequestDto));
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signupSuccess() {
        // given
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());
        Member member = signupRequestDto.toMember();
        member.setId(1);
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // when
        ApiResponseDto apiResponseDto = memberService.signup(signupRequestDto);

        // then
        then(memberRepository).should().findByEmail(anyString());
        then(memberRepository).should().save(any(Member.class));
        then(memberRepository).shouldHaveNoMoreInteractions();

        SignupResponseDto signupResponseDto = (SignupResponseDto)apiResponseDto.getData();
        assertEquals(member.getId(), signupResponseDto.getId());
        assertEquals(member.getName(), signupResponseDto.getName());
        assertEquals(member.getEmail(), signupResponseDto.getEmail());
        assertEquals(member.getMobileNumber(), signupResponseDto.getMobileNumber());
        assertEquals(member.getRole(), signupResponseDto.getRole());
    }
}
