package gongnon.domain.data.dto;

import lombok.Data;

@Data
public class NewsArticleDto {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;
}
