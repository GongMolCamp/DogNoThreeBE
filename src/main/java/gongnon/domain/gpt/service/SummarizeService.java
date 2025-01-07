package gongnon.domain.gpt.service;

import gongnon.domain.gpt.dto.ChatGPTRequest;
import gongnon.domain.gpt.dto.ChatGPTResponse;
import gongnon.domain.gpt.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SummarizeService {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    // "openAiRestTemplate"로 주입받아야 Authorization 헤더가 자동으로 붙음
    @Qualifier("openAiRestTemplate")
    private final RestTemplate restTemplate;

    /**
     * 뉴스 기사 요약 로직
     */
    public String summarize(String title, String content) {
        // 1) 프롬프트 생성
        String prompt = String.format(
                "기사의 제목이 \"%s\"이며 본문이 \"%s\"인 기사의 가장 중요한 내용을 3문장으로 요약해 주세요.",
                title, content
        );

        // 2) DTO 생성
        ChatGPTRequest request = new ChatGPTRequest();
        request.setModel(model);
        request.setMessages(List.of(new Message("user", prompt)));

        // 3) OpenAI API 호출
        ChatGPTResponse response = restTemplate.postForObject(apiURL, request, ChatGPTResponse.class);

        // 4) 응답 검증
        if (response == null || response.getChoices().isEmpty()) {
            return "GPT 응답이 없습니다.";
        }

        return response.getChoices().get(0).getMessage().getContent();
    }
}
