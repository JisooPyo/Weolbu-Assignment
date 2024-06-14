package weolbu.assignment.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import weolbu.assignment.common.constants.MemberConstants;
import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.common.exception.CustomErrorCode;
import weolbu.assignment.common.exception.CustomException;
import weolbu.assignment.member.dto.SignupRequestDto;
import weolbu.assignment.member.entity.Member;
import weolbu.assignment.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponseDto signup(SignupRequestDto requestDto) {
        checkEmailRegistered(requestDto.getEmail());
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        Member member = requestDto.toMember(encodedPassword);
        Member savedMember = memberRepository.save(member);
        return new ApiResponseDto(MemberConstants.SIGNUP_SUCCESS, savedMember.toSignupResponseDto());
    }

    // 가입된 계정인지 확인합니다.
    private void checkEmailRegistered(String email) {
        Member member = findMemberByEmail(email);
        if (member != null) {
            throw new CustomException(CustomErrorCode.MEMBER_ALREADY_EXISTS);
        }
    }

    // 이메일로 회원 정보를 불러옵니다.
    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }
}
