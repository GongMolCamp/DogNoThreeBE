package gongnon.domain.gpt.controller;

import gongnon.domain.gpt.dto.ChatGPTRequest;
import gongnon.domain.gpt.dto.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/bot")
public class CustomBotController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    // "openAiRestTemplate"를 주입받아야 Bearer 헤더가 포함됨
    private final RestTemplate restTemplate;

    @Autowired
    public CustomBotController(@Qualifier("openAiRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 테스트용 엔드포인트
     * ex) GET /bot/chat?prompt=Hello
     */
    @GetMapping("/chat")
    public String chat(@RequestParam(name = "prompt") String prompt) {
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);

        ChatGPTResponse chatGPTResponse = restTemplate.postForObject(apiURL, request, ChatGPTResponse.class);

        if (chatGPTResponse == null || chatGPTResponse.getChoices().isEmpty()) {
            return "GPT 응답이 없습니다.";
        }
        return chatGPTResponse.getChoices().get(0).getMessage().getContent();
    }
}
