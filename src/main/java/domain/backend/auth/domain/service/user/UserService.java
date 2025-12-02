package domain.backend.auth.domain.service.user;

import domain.backend.auth.domain.user.User;
import domain.backend.auth.dto.request.user.UserSignupRequest;
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
}
