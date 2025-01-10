package gongnon.domain.sns.service;

import gongnon.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final RedisUtil redisUtil;
    private DefaultMessageService messageService;

    @Value("${coolsms.apikey}")
    private String apiKey;

    @Value("${coolsms.secretkey}")
    private String apiSecret;

    @PostConstruct
    public void initMessageService() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendSms(String phoneNumber) {
        String randomNum = createRandomNumber();

        // Redis에 인증번호 저장 (유효 시간 5분)
        redisUtil.set(phoneNumber, randomNum, 5);

        // SMS 메시지 구성
        Message message = new Message();
        message.setFrom("01062476416"); // 발신번호
        message.setTo(phoneNumber); // 수신번호
        message.setText("[Gongnon] 인증번호 [" + randomNum + "]를 입력해 주세요.");

        // 메시지 전송
        try {
            SingleMessageSendingRequest request = new SingleMessageSendingRequest(message);
            SingleMessageSentResponse response = this.messageService.sendOne(request);

            System.out.println("SMS 전송 성공: " + response);
        } catch (Exception e) {
            throw new RuntimeException("SMS 전송 실패: " + e.getMessage());
        }
    }

    public boolean validateSms(String phoneNumber, String inputCode) {
        String storedCode = redisUtil.get(phoneNumber);
        return storedCode != null && storedCode.equals(inputCode);
    }

    private String createRandomNumber() {
        return String.valueOf(1000 + new Random().nextInt(9000)); // 4자리 랜덤 숫자 생성
    }
}
