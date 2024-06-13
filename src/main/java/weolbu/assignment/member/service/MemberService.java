package weolbu.assignment.member.service;

import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.member.dto.SignupRequestDto;

public interface MemberService {
    /**
     * 필요한 정보를 받아 유저 정보를 등록합니다.
     *
     * @param requestDto 회원가입에 필요한 유저 정보
     * @return 회원가입 성공 메시지와 회원가입된 유저 정보
     */
    ApiResponseDto signup(SignupRequestDto requestDto);
}
