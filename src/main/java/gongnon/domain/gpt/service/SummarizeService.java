package gongnon.domain.gpt.service;

import gongnon.domain.gpt.dto.ChatGPTRequest;
import gongnon.domain.gpt.dto.ChatGPTResponse;
import gongnon.domain.gpt.dto.Message;
import gongnon.domain.gpt.model.NewsArticle;
import gongnon.domain.gpt.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final NewsArticleRepository newsArticleRepository;

    /**
     * 뉴스 기사 요약 로직
     */
    @Transactional
    public String summarize(String title, String content) {
        // 1) 프롬프트 생성
        String prompt = String.format(
                "기사의 제목이 \"%s\"이며 본문이 \"%s\"인 기사의 가장 중요한 내용을 1문장으로 요약해 주세요.",
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

        // 5) 요약된 내용 DB에 저장
        newsArticleRepository.save(new NewsArticle(title, content, response.getChoices().get(0).getMessage().getContent()));

        // 6) 요약된 내용 조회
        String title1 = newsArticleRepository.findById(1L).get().getTitle();
        System.out.println("title1 = " + title1);
        String content1 = newsArticleRepository.findById(1L).get().getContent();
        System.out.println("content1 = " + content1);

        return response.getChoices().get(0).getMessage().getContent();
    }

    @Transactional
    public String summarize(Long id) {
        // 1) db에서 id로 기사 조회
        NewsArticle article = newsArticleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID의 기사가 존재하지 않습니다."));

        // 2) 프롬프트 생성
        String prompt = String.format(
                "기사의 제목이 \"%s\"이며 본문이 \"%s\"인 기사의 가장 중요한 내용을 1문장으로 요약해 주세요.",
                article.getTitle(), article.getContent()
        );

        // 3) DTO 생성
        ChatGPTRequest request = new ChatGPTRequest();
        request.setModel(model);
        request.setMessages(List.of(new Message("user", prompt)));

        // 4) OpenAI API 호출
        ChatGPTResponse response = restTemplate.postForObject(apiURL, request, ChatGPTResponse.class);

        // 5) 응답 검증
        if (response == null || response.getChoices().isEmpty()) {
            return "GPT 응답이 없습니다.";
        }

        // 5) 요약된 내용 DB에 저장
        article.setSummary(response.getChoices().get(0).getMessage().getContent());
        newsArticleRepository.save(article);

        // 6) 요약된 내용 조회

        return response.getChoices().get(0).getMessage().getContent();
    }
}
