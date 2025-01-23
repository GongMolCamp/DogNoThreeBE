package gongnon.configuration;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
	info = @Info(
		title = "개노삼 BE Swagger",
		version = "1.0",
		description = "개노삼 백엔드 API 명세서입니다."
	)
)
public class SwaggerConfig {
}
