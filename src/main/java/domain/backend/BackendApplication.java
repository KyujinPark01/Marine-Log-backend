package domain.backend;

import domain.backend.auth.domain.user.Role;
import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import domain.backend.common.config.AdminProperties;
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
    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository,
                                       PasswordEncoder passwordEncoder,
                                       AdminProperties adminProps) {
        return args -> {
            boolean exists = userRepository.existsByEmailAndRole(adminProps.getEmail(), Role.ADMIN);

            if (exists) {
                return;
            }

            User admin = User.builder()
                    .email(adminProps.getEmail())
                    .password(passwordEncoder.encode(adminProps.getPassword()))
                    .name(adminProps.getName())
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
        };
    }

}
