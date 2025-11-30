package domain.backend.auth.controller.admin;


import domain.backend.auth.domain.service.admin.AdminAuthService;
import domain.backend.auth.dto.request.admin.AdminLoginRequest;
import domain.backend.auth.dto.response.admin.AdminLoginResponse;
import domain.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminAuthService adminAuthService;
    @PostMapping("login")
    public ResponseEntity<ApiResponse<?>> login (@RequestBody AdminLoginRequest adminLoginRequest){
        AdminLoginResponse loginResponse = adminAuthService.login(adminLoginRequest);
        return ResponseEntity.ok(ApiResponse.ok(loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout() {
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
