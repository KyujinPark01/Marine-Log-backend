package domain.backend.auth.controller.user;

import domain.backend.auth.domain.service.user.UserService;
import domain.backend.auth.domain.user.Role;
import domain.backend.auth.dto.request.user.UserSignupRequest;
import domain.backend.auth.dto.response.user.UserResponse;
import domain.backend.auth.dto.response.user.UserSignupResponse;
import domain.backend.common.dto.ApiResponse;
import domain.backend.common.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {


    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "Access Token을 통해 로그인한 사용자의 정보를 반환합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me( @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse response = UserResponse.builder()
                .uuid(principal.uuid())
                .email(principal.email())
                .name(principal.name())
                .role(Role.valueOf(principal.role()))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("signup")
    public ResponseEntity<ApiResponse<?>> signupAdditional(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UserSignupRequest userSignupRequest) {
        UserSignupResponse userSignupResponse = userService.signupAdditional(principal.userId(), userSignupRequest);
        return ResponseEntity.ok(ApiResponse.ok(userSignupResponse));
    }
}