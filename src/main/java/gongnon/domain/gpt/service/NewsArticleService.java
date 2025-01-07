package gongnon.domain.gpt.service;

import gongnon.domain.gpt.model.NewsArticle;
import gongnon.domain.gpt.repository.NewsArticleRepository;
import gongnon.domain.gpt.service.SummarizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;


@Service
@RequiredArgsConstructor
public class NewsArticleService {

    private final NewsArticleRepository newsArticleRepository;
    private final SummarizeService summarizeService;

    public NewsArticle createAndSummarize(String title, String content){
        NewsArticle newsArticle = new NewsArticle();
        newsArticle.setTitle(title);
        newsArticle.setContent(content);

        String summary = summarizeService.summarize(title, content);
        newsArticle.setSummary(summary);

        return newsArticleRepository.save(newsArticle);

    }

    public NewsArticle getById(Long id) {
        return newsArticleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found. ID=" + id));
    }

    public NewsArticle updateArticle(Long id, String newContent) {
        NewsArticle article = getById(id);
        article.setContent(newContent);

        // 재요약
        String newSummary = summarizeService.summarize(article.getTitle(), newContent);
        article.setSummary(newSummary);

        return newsArticleRepository.save(article);
    }


}
