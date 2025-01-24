package gongnon.domain.hotnews.controller;

import gongnon.domain.hotnews.model.UserPreferPress;
import gongnon.domain.hotnews.model.PredefinedPress;
import gongnon.domain.hotnews.service.UserPreferPressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/prefer-press")
public class UserPreferController {
    @Autowired
    private UserPreferPressService userPreferPressService;

    // 사용자의 현재 선호 언론사 조회
    @GetMapping("/{userId}")
    public UserPreferPress getUserPreferPress(@PathVariable long userId) {
        return userPreferPressService.getUserPreferPress(userId);
    }

    // 사용자의 선호 언론사 변경
    @PutMapping
    public UserPreferPress updateUserPreferPress(@RequestBody Map<String, Long> requestData) {
        Long userId = requestData.get("userId");
        Long newPressId = requestData.get("newPressId");
        return userPreferPressService.updateUserPreferPress(userId, newPressId);
    }
}
