package gongnon.domain.hotnews.service;

import gongnon.domain.hotnews.model.HotNewsArticle;
import gongnon.domain.hotnews.model.HotNewsArticleComment;
import gongnon.domain.hotnews.model.PredefinedPress;
import gongnon.domain.hotnews.repository.HotNewsArticleRepository;
import gongnon.domain.hotnews.repository.HotNewsArticleCommentRepository;
import gongnon.domain.hotnews.repository.PredefinedPressRepository;
import gongnon.domain.hotnews.service.HotNewsSummarizeService;
import gongnon.domain.appUser.model.AppUser;
import gongnon.domain.appUser.repository.AppUserRepository;
import gongnon.domain.gpt.service.SummarizeService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

@Service
public class HotNewsArticleService {
    @Autowired
    private HotNewsArticleRepository hotNewsArticleRepository;

    @Autowired
    private PredefinedPressRepository predefinedPressRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private HotNewsArticleCommentRepository hotNewsArticleCommentRepository;

    @Autowired
    private HotNewsSummarizeService hotNewsSummarizeService;

    @Transactional
    public void updateAllPressTop5News(LocalDate date) {
        // 언론사 전체 조회
        List<PredefinedPress> presses = predefinedPressRepository.findAll();

        // 각 언론사별로 updateTop5News() 호출
        for (PredefinedPress press : presses) {
            updateTop5News(press.getId(), date);
        }
    }
    // 사용자 선호 언론사 + 날짜 기준 탑5 뉴스 조회
    @Transactional
    public List<HotNewsArticle> getTop5NewsByPress(Long userId, LocalDate date){
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));

        PredefinedPress preferPress = user.getPreferPress();
        if (preferPress == null){
            throw new IllegalArgumentException("선호 언론사가 설정되지 않았습니다");
        }

        // 언론사 + 날짜 기준으로 뉴스 조회
        List<HotNewsArticle> articles = hotNewsArticleRepository.findByNewsDateAndPress_Id(date, preferPress.getId());

        // 뉴스가 없으면 업데이트 진행
        if (articles.isEmpty()){
            updateTop5News(preferPress.getId(), date);
            articles = hotNewsArticleRepository.findByNewsDateAndPress_Id(date, preferPress.getId());
        }

        return articles;
    }

    public void updateTop5News(Long pressId, LocalDate date) {
        PredefinedPress press = predefinedPressRepository.findById(pressId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 언론사입니다."));

        hotNewsArticleRepository.deleteByNewsDate(date);

        List<HotNewsArticle> top5Articles = fetchTop5News(press, date);

        for (HotNewsArticle article : top5Articles) {
            article.setPress(press);
            article.setNewsDate(date);

            hotNewsArticleRepository.save(article);
        }
    }
    // fetchTop5News 구현
    private List<HotNewsArticle> fetchTop5News(PredefinedPress press, LocalDate date){
        List<HotNewsArticle> articles = new ArrayList<>();

        try{
            // 언론사 pressId 기반 URL 설정
            String pressId = press.getPressId();
            if (pressId == null || pressId.isEmpty()){
                System.out.println("PredefinedPress ID is null or empty for press: " + press.getName()); // 디버깅
                throw new IllegalArgumentException("Invalid press ID");
            } // 디버깅
            String url = "https://media.naver.com/press/" + pressId + "/ranking?type=popular";
            System.out.println("Fetching URL: " + url); // 디버깅
            // Jsoup 활용 html 크롤링
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get(); // 위에 코드 디버깅
            Elements newsElements = document.select("li.as_thumb");

            // 상위 5개 뉴스 추출
            for (int i = 0; i< Math.min(5, newsElements.size()); i++){
                Element newsElement = newsElements.get(i);

                // 기사 제목, 링크, 이미지 url 추출
                String title = newsElement.select(".list_title").text();
                String link = newsElement.select("a").attr("href");
                String imageUrl = newsElement.select("img").attr("src");
                int rank = i + 1;

                // 기사 본문 크롤링
                String content = fetchArticleContent(link);

                // 요약 호출
                String summary = hotNewsSummarizeService.summarizeByContent(title, content);
                // HotNewsArticle 객체 생성
                HotNewsArticle article = new HotNewsArticle();
                article.setTitle(title);
                article.setLink(link);
                article.setImageUrl(imageUrl);
                article.setSummary(summary);
                article.setNewsDate(date);
                article.setPress(press);
                article.setRanking(rank);
                article.setContent(content);

                articles.add(article);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("뉴스 크롤링에 실패했습니다.");
        }
        return articles;
    }

    // 본문(content) 크롤링 메소드
    private String fetchArticleContent(String articleUrl) {
        try {
            // 기사 페이지 이동하여 본문 데이터 추출
            Document articleDoc = Jsoup.connect(articleUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();

            // 여러 CSS 선택자를 통해 본문 요소 찾기
            String[] selectors = { "div#newsct_article", "article#dic_area", "div#dic_area", "div#contents" };
            Element articleBody = null;
            for (String sel : selectors) {
                articleBody = articleDoc.selectFirst(sel);
                if (articleBody != null) break;
            }

            // 본문 텍스트 처리
            String fullContent = "본문을 가져올 수 없습니다.";
            if (articleBody != null) {
                // <p> 태그 기준으로 합치되, <br>은 개행으로 치환
                Elements paragraphs = articleBody.select("p");
                if (!paragraphs.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Element p : paragraphs) {
                        // <br> 처리
                        String pHtml = p.html().replaceAll("<br ?/?>", "\n");
                        sb.append(Jsoup.parse(pHtml).text()).append("\n");
                    }
                    fullContent = sb.toString().trim();
                } else {
                    // <p> 태그가 없는 경우 전체에서 <br> 처리
                    String bodyHtml = articleBody.html().replaceAll("<br ?/?>", "\n");
                    fullContent = Jsoup.parse(bodyHtml).text().trim();
                }
            }

            return fullContent;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to fetch article content from: " + articleUrl);
        }

        return "본문을 가져오는 데 실패했습니다.";
    }

    // 댓글 추가
    public HotNewsArticleComment addCommentToArticle(Long articleId, String author, String content){
        HotNewsArticle article = hotNewsArticleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기사입니다."));

        HotNewsArticleComment comment = new HotNewsArticleComment();
        comment.setArticle(article);
        comment.setAuthor(author);
        comment.setContent(content);

        return hotNewsArticleCommentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        if (!hotNewsArticleCommentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다");
        }

        hotNewsArticleCommentRepository.deleteById(commentId);
    }

    // 댓글 조회
    public List<HotNewsArticleComment> getCommentsByArticleId(Long articleId) {
        HotNewsArticle article = hotNewsArticleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기사입니다"));

        return article.getComments();
    }
}
