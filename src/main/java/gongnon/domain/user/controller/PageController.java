package gongnon.domain.user.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import gongnon.domain.sms.model.Message;
import gongnon.domain.sms.repository.SmsRepository;
import gongnon.domain.user.model.User;
import gongnon.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {
	private final UserService userService;
	private final SmsRepository smsRepository;

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
	public ModelAndView register(
		@Valid User user,        // User 객체에 대한 검증(@NotBlank, @Pattern 등)을 수행
		BindingResult bindingResult
	) {
		if (bindingResult.hasErrors()) {
			// 유효성 검증 실패 시, 회원가입 폼으로 다시 보냄
			// 또는 에러 메시지를 모델에 담아 재표출하는 등 처리
			return new ModelAndView("register");
		}

		userService.registerUser(user); // 회원가입 처리
		return new ModelAndView("redirect:/");
	}

	@GetMapping("/mypage")
	public String myPage(Model model, HttpSession session) {
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser != null) {
			// 세션에 저장된 유저 정보를 모델에 추가
			model.addAttribute("user", loggedInUser);

			// 현재 유저의 메시지 기록 조회 및 모델에 추가
			List<Message> messages = smsRepository.findByUser(loggedInUser);
			model.addAttribute("messages", messages);

			return "mypage"; // -> resources/templates/mypage.html
		} else {
			return "redirect:/"; // 로그인 안 된 경우 홈으로 리다이렉트
		}
	}
}
