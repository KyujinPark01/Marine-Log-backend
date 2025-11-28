package domain.backend.auth.aop;

import domain.backend.common.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;

    @Around("@annotation(domain.backend.auth.aop.AuthHeader)")
    public Object checkToken(ProceedingJoinPoint joinPoint) throws Throwable {

        String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);

        if (!jwtTokenProvider.validate(token)) {
            return ResponseEntity.status(401).build();
        }

        Claims claims = jwtTokenProvider.getClaims(token);
        request.setAttribute("claims", claims);

        return joinPoint.proceed();
    }
}

