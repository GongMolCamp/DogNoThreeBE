package gongnon.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@RefreshScope
@Configuration
public class OpenAiConfig {
    @Value("${openai.api.key1}")
    private String openAiKey;


    /**
     * "OpenAI 전용" RestTemplate
     * -> Authorization: Bearer {openAiKey}
     * -> Content-Type: application/json
     */
    @Bean("openAiRestTemplate")
    @Primary
    public RestTemplate openAiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            // 디버깅 출력
            System.out.println("[DEBUG] Request URI: " + request.getURI());

            System.out.println("[DEBUG] Body: " + new String(body));

            // 기본 인증/헤더
            request.getHeaders().add("Authorization", "Bearer " + openAiKey);
            request.getHeaders().set("Content-Type", "application/json");

            System.out.println("[DEBUG] Request Headers: " + request.getHeaders());

            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
