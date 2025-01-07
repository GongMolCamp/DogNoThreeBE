package gongnon.domain.gpt.dto;

import gongnon.domain.gpt.model.NewsArticle;
import lombok.Data;

@Data
public class NewsArticleDto {
    private Long id;
    private String title;
    private String content;
    private String summary;

    public NewsArticleDto(){

    };
    public NewsArticleDto(NewsArticle entity){
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.summary = entity.getSummary();
    }
}
