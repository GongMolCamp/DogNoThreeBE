package gongnon.domain.sms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gongnon.domain.sms.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "SMS", description = "SMS 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {
    private final SmsService smsService;

    @GetMapping("/send")
    @Operation(summary = "SMS 문자메시지 전송하기")
    public void sendSms(
        @Parameter(description = "받는 사람 번호", required = true, example = "01012345678")
        @RequestParam("to") String to,
        @Parameter(description = "메시지 내용", required = true, example = "안녕하세요")
        @RequestParam("message") String message
    ) {
        smsService.sendSms(to, message);
    }
}
