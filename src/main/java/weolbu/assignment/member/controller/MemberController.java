package weolbu.assignment.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.member.dto.SignupRequestDto;
import weolbu.assignment.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto){
        ApiResponseDto response = memberService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
