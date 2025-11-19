package domain.backend.common.security.jwt;

import domain.backend.auth.domain.user.User;
import domain.backend.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserRepository userRepository;
    @Value("${jwt.secret}")
    private String secretBase64;
    @Value("${jwt.access-exp}")
    private long accessExpSeconds;
    @Value("${jwt.refresh-exp}")
    private long refreshExpSeconds;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
    }

    public String createAccessToken(String subjectEmail, Map<String, Object> claims) {
        return createToken(subjectEmail, claims, accessExpSeconds);
    }

    public String createRefreshToken(String subjectEmail) {
        return createToken(subjectEmail, Map.of("typ","refresh"), refreshExpSeconds);
    }

    private String createToken(String subject, Map<String, Object> claims, long expSec) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expSec)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
    public void updateRefreshToken(String email, String refreshToken) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + email));
        userRepository.save(user.updateRefreshToken(refreshToken));
    }

    public String getSubject(String token) {

        return getClaims(token).getSubject();
    }
}
