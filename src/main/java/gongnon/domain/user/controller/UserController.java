package gongnon.domain.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import gongnon.domain.user.model.User;
import gongnon.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/login")
	@Operation(summary = "로그인")
	public ResponseEntity<String> login(
		User user,
		HttpSession session
	) {
		Optional<User> authenticatedUser = userService.loginUser(user.getUsername(), user.getPassword());
		if (authenticatedUser.isPresent()) {
			session.setAttribute("loggedInUser", authenticatedUser.get());
			return ResponseEntity.ok("로그인 성공!");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
		}
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	public ResponseEntity<String> logout(HttpSession session) {
		session.invalidate();  // 세션 무효화
		return ResponseEntity.ok("로그아웃 성공!");
	}

	@PostMapping("/updatePreferences")
	@Operation(summary = "사용자 설정 업데이트")
	public ResponseEntity<String> updatePreferences(
		@Parameter(description = "키워드", required = true, example = "경제")
		@RequestParam String newsPreference,
		@Parameter(description = "알림 시간", required = true, example = "09:00")
		@RequestParam String notificationTime,
		HttpSession session
	) {
		// 세션에서 로그인 사용자 가져오기
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return ResponseEntity.badRequest().body("로그인이 필요합니다.");
		}

		// 기존 User 객체 갱신
		loggedInUser.setNewsPreference(newsPreference);
		loggedInUser.setNotificationTime(notificationTime);

		// DB 반영
		userService.updateUser(loggedInUser);

		// 세션 사용자도 최신 상태로 갱신
		session.setAttribute("loggedInUser", loggedInUser);

		return ResponseEntity.ok("업데이트 성공!");
	}
}
