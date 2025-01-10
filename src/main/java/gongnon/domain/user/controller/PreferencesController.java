package gongnon.domain.user.controller;

import gongnon.domain.user.model.User;
import gongnon.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PreferencesController {

	private final UserService userService;

	// 전통적인 POST 폼 전송 시 여기로 옴
	@PostMapping("/updatePreferencesForm")
	public String updatePreferencesForm(
		@RequestParam String newsPreference,
		@RequestParam String notificationTime,
		HttpSession session
	) {
		// 세션에서 로그인 유저 얻기
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			// 비로그인 상태면 홈으로
			return "redirect:/";
		}

		// User 엔티티 갱신
		loggedInUser.setNewsPreference(newsPreference);
		loggedInUser.setNotificationTime(notificationTime);

		// DB 반영
		userService.updateUser(loggedInUser);

		// 세션도 최신 상태로 갱신
		session.setAttribute("loggedInUser", loggedInUser);

		// 마이페이지로 다시 리다이렉트
		return "redirect:/mypage";
	}
}
