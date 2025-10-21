package domain.backend.auth.repository;

import domain.backend.auth.domain.user.Provider;
import domain.backend.auth.domain.user.SocialLogin;
import domain.backend.auth.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialLoginRepository extends JpaRepository<SocialLogin, String> {
    Optional<SocialLogin> findByUserAndProvider(User user, Provider provider);


}
