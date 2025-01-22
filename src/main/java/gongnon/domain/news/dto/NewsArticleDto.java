package gongnon.domain.news.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewsArticleDto {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;
}
