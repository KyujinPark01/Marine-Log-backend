package domain.backend.common.config;

import domain.backend.common.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Configuration
public class RestClientConfig {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;
    private static final String KAKAO_BASE_URL = "https://dapi.kakao.com";

    @Bean
    public RestClient kakaoRestClient() {
        return RestClient.builder()
                .baseUrl(KAKAO_BASE_URL)
                .defaultHeader("Authorization", "KakaoAK " +  kakaoApiKey)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    throw new ExternalApiException(
                            "Kakao API error " + response.getStatusCode()
                    );
                })
                .build();
    }
}
