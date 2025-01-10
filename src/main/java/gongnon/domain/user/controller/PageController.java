package gongnon.domain.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import gongnon.domain.user.model.User;
import gongnon.domain.user.service.UserService;

@Controller
public class PageController {
	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String homePage(Model model) {
		model.addAttribute("message", "뉴스 요약 및 문자 전송 플랫폼에 오신 것을 환영합니다!!");
		return "home"; // resources/templates/home.html로 매핑됨
	}

	@GetMapping("/register")
	public String registerPage() {
		return "register"; // resources/templates/register.html로 매핑됨
	}

	@PostMapping("/register")
	public ModelAndView register(User user) {
		userService.registerUser(user); // 회원가입 처리
		return new ModelAndView("redirect:/"); // 회원가입 성공 후 메인 페이지로 리다이렉트
	}
}
