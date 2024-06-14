package weolbu.assignment.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import weolbu.assignment.common.dto.ApiResponseDto;
import weolbu.assignment.member.dto.SignupRequestDto;
import weolbu.assignment.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "Member API", description = "Member 관련 API 정보를 담고 있습니다.")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원가입 API")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        ApiResponseDto response = memberService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
