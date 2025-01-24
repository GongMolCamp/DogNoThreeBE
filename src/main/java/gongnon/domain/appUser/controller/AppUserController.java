package gongnon.domain.appUser.controller;

import gongnon.domain.appUser.model.AppUser;
import gongnon.domain.appUser.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/app-users")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @PostMapping("/register")
    public String registerUser(@RequestBody AppUser user) {
        appUserService.registerUser(user);
        return "사용자가 성공적으로 등록되었습니다.";
    }

    @GetMapping("/{email}")
    public AppUser getUserByEmail(@PathVariable String email) {
        return appUserService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
