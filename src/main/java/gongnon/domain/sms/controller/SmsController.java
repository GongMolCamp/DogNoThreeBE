package gongnon.domain.sms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gongnon.domain.sms.service.SmsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {
    private final SmsService smsService;

    @GetMapping("/send")
    public void sendSms(
        @RequestParam("to") String to,
        @RequestParam("message") String message
    ) {
        smsService.sendSms(to, message);
    }
}
