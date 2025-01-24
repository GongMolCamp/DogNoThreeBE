package gongnon.domain.hotnews.controller;

import gongnon.domain.hotnews.model.PredefinedPress;
import gongnon.domain.hotnews.service.PredefinedPressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/press")
public class PredefinedPressController {
    @Autowired
    private PredefinedPressService predefinedPressService;

    @GetMapping("/all")
    public List<PredefinedPress> getAllPress() {
        return predefinedPressService.getAllPress();
    }
}
