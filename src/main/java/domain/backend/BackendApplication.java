package domain.backend;

import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
@EnableMethodSecurity
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
//    @Bean
//    public CommandLineRunner initAdmin(UserRepository userRepository,
//                                       PasswordEncoder passwordEncoder) {
//        return args -> {
//            boolean exists = userRepository.existsByEmailAndRole("admin@naver.com", Role.ADMIN);
//            if (exists) {
//                System.out.println("✔ Admin already exists. Skip seeding.");
//                return;
//            }
//            User admin = User.builder()
//                    .email("admin@naver.com")
//                    .password(passwordEncoder.encode("admin1234"))
//                    .name("관리자")
//                    .role(Role.ADMIN)
//                    .address(null)
//                    .phoneNumber(null)
//                    .refreshToken(null)
//                    .refreshTokenExpiresAt(null)
//                    .organization(null)
//                    .build();
//
//            userRepository.save(admin);
//
//            System.out.println("🎉 Admin account created: admin@naver.com / admin1234");
//        };
//    }

}
