package gongnon.domain.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import gongnon.domain.user.model.User;
import gongnon.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user, HttpSession session) {
		Optional<User> authenticatedUser = userService.loginUser(user.getUsername(), user.getPassword());
		if (authenticatedUser.isPresent()) {
			session.setAttribute("loggedInUser", authenticatedUser.get());
			return ResponseEntity.ok("로그인 성공!");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpSession session) {
		session.invalidate(); // 세션 무효화
		return ResponseEntity.ok("로그아웃 성공!");
	}
}
