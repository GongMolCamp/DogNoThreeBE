package gongnon.domain.data.dto;

import gongnon.domain.data.model.NewsArticle;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NewsArticleDto {
    private Long id;
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;

    public NewsArticleDto(NewsArticle entity){
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.link = entity.getLink();
        this.pubDate = entity.getPubDate().toString();
    }
}