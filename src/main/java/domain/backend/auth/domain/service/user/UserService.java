package domain.backend.auth.domain.service.user;

import domain.backend.auth.domain.user.User;
import domain.backend.auth.dto.request.user.UserSignupRequest;
import domain.backend.auth.dto.response.user.UserResponse;
import domain.backend.auth.dto.response.user.UserSignupResponse;
import domain.backend.auth.repository.UserRepository;
import domain.backend.organization.domain.entity.Organization;
import domain.backend.organization.repository.OrganizationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    @Transactional
    public UserSignupResponse signupAdditional(Long userId, UserSignupRequest userSignupRequest){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Organization org = organizationRepository.findById(userSignupRequest.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("단체 없음"));

        user.updateAdditionalInfo(
                org,
               userSignupRequest.getPhoneNumber(),
                userSignupRequest.getAddress()
        );
        return new UserSignupResponse(
                userSignupRequest.getOrganizationId(),
                userSignupRequest.getPhoneNumber(),
                userSignupRequest.getAddress());
    }
    // 유저 정보 조회 메서드 추가(마이페이지에 필요)
    @Transactional
    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        return UserResponse.builder()
                .id(user.getUserId())
                .uuid(user.getUuid())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .organizationName(user.getOrganization() != null ?
                        user.getOrganization().getName() : null)
                .organizationId(user.getOrganization() != null ?
                        user.getOrganization().getOrgId() : null)
                .build();
    }
}
