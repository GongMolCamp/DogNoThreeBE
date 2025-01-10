package gongnon.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
	@GetMapping("/")
	public String homePage(Model model) {
		model.addAttribute("message", "Hello, Spring with Thymeleaf!");
		return "home"; // resources/templates/index.html로 매핑됨
	}
}
