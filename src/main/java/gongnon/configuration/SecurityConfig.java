package gongnon.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/register", "/api/users/**", "/css/**", "/js/**", "/images/**").permitAll()
				.requestMatchers("/api/users/register").permitAll()
				.anyRequest().authenticated()
			)
			.httpBasic(httpBasic -> httpBasic.disable())
			.formLogin(form -> form.disable());

		return http.build();
	}
}
