package domain.backend.auth.repository;


import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User>  findByEmailAndRole(String email, Role role);
    boolean existsByEmailAndRole(String email, Role role);
    Optional<User> findByUuid(UUID uuid);
    Optional<User> findByUuidAndRole(UUID uuid, Role role);



}
