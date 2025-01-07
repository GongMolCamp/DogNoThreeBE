package gongnon.domain.data.service;

import gongnon.domain.data.dto.NewsArticleDto;
import gongnon.domain.data.dto.NewsResponseDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsArticleScrapService {
    public NewsResponseDto scrapingArticleBody(List<NewsArticleDto> articleDtos) {
        for (NewsArticleDto articleDto : articleDtos) {
            try{
                String articleUrl = articleDto.getLink();
                Document naverNews = Jsoup.connect(articleUrl).get();
                Element articleBody = naverNews.selectFirst("#newsct_article");

                if (articleBody != null) {
                    articleDto.setDescription(articleBody.text()); // 본문 데이터를 저장
                } else {
                    articleDto.setDescription("기사 본문을 가져오는데 실패하였습니다."); // 실패 메시지
                }
            } catch (Exception e) {
                articleDto.setDescription("크롤링 중 오류 발생: " + e.getMessage()); // 오류 메시지 저장
            }
        }
        return new NewsResponseDto(articleDtos);
    }
}
