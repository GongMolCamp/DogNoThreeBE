package gongnon.domain.sns.controller;

import gongnon.domain.sns.dto.CertificateDto;
import gongnon.domain.sns.dto.SmsDto;
import gongnon.domain.sns.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    /**
     * SMS 인증번호 전송
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendSms(@RequestBody SmsDto smsDto) {
        System.out.println("Request received with phoneNumber: " + smsDto.getPhoneNumber());
        smsService.sendSms(smsDto.getPhoneNumber().replace("-", ""));
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    /**
     * SMS 인증번호 검증
     */
    @PostMapping("/validate")
    public ResponseEntity<String> validateSms(@RequestBody CertificateDto certificateDto) {
        boolean isValid = smsService.validateSms(certificateDto.getPhoneNumber(), certificateDto.getCertificateNum());

        if (isValid) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.status(400).body("인증 실패");
        }
    }
}
