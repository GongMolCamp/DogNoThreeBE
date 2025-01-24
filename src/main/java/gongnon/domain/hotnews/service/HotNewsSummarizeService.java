package gongnon.domain.hotnews.service;

import gongnon.domain.gpt.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotNewsSummarizeService {
    // application.yml에 들어있는 값 재활용
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    // "openAiRestTemplate"를 주입받으면, 이미 Authorization 헤더가 설정되어 있음
    @Qualifier("openAiRestTemplate")
    private final RestTemplate restTemplate;

    // 제목과 본문을 받아 GPT 요약문 반환하는 메소드
    public String summarizeByContent(String title, String content) {
        // 프롬프트 구성
        String prompt = String.format(
                "기사 제목: \"%s\"\n" +
                        "본문: \"%s\"\n" +
                        "위 기사를 한 문장으로 간결하게 요약해 주세요. (존댓말 사용)",
                title, content
        );

        // ChatGPTRequest 생성
        ChatGPTRequest request = new ChatGPTRequest();
        request.setModel(model);
        request.setMessages(List.of(
                new Message("user", prompt)
        ));

        // GPT API 호출
        ChatGPTResponse response = restTemplate.postForObject(apiUrl, request, ChatGPTResponse.class);

        // 응답 검증
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "GPT 응답이 없습니다.";
        }

        // 요약문 추출 및 반환
        return response.getChoices().get(0).getMessage().getContent().trim();
    }
}
