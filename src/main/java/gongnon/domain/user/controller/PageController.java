package gongnon.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import gongnon.domain.user.model.User;
import gongnon.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {
	private final UserService userService;

	@GetMapping("/")
	public String homePage(Model model) {
		model.addAttribute("message", "뉴스 요약 및 문자 전송 플랫폼에 오신 것을 환영합니다!!");
		return "home"; // resources/templates/home.html
	}

	@GetMapping("/registerPage")
	public String register(Model model) {
		return "register"; // resources/templates/register.html
	}

	@PostMapping("/register")
	public ModelAndView register(User user) {
		userService.registerUser(user); // 회원가입 처리
		// 회원가입 완료 후 home.html 로 이동
		return new ModelAndView("redirect:/");
	}

	@GetMapping("/mypage")
	public String myPage(Model model, HttpSession session) {
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser != null) {
			model.addAttribute("user", loggedInUser);
			return "mypage"; // resources/templates/mypage.html
		} else {
			// 로그인 안 된 경우 홈으로 리다이렉트
			return "redirect:/";
		}
	}
}
