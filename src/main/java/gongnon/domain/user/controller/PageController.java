package gongnon.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
	@GetMapping("/")
	public String homePage(Model model) {
		model.addAttribute("message", "뉴스 요약 및 문자 전송 플랫폼에 오신 것을 환영합니다!!");
		return "home"; // resources/templates/index.html로 매핑됨
	}
}
