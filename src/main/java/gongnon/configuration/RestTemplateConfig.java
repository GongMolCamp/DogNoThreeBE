package gongnon.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * "일반 용도" RestTemplate
     * -> Authorization 헤더 X
     * -> 필요 없다면 이 파일 자체를 제거해도 됨
     */
    @Bean("normalRestTemplate")
    public RestTemplate normalRestTemplate() {
        return new RestTemplate();
    }
}
