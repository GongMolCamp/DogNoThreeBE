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
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				// 홈, 회원가입 페이지, 회원가입 처리, 정적 리소스 등은 익명 접근 허용
				.requestMatchers(
					"/**").permitAll()
				// 필요하다면, "/mypage"도 로그인 없이 접근할지 여부에 따라 열거나 막을 수 있음
				.anyRequest().authenticated()
			)
			.httpBasic(httpBasic -> httpBasic.disable())
			.formLogin(form -> form.disable());

		return http.build();
	}
}
