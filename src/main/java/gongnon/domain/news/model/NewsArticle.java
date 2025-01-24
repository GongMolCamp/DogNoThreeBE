package gongnon.domain.news.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "news_article")
public class NewsArticle {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String newsLink;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String summary;

    private LocalDateTime publicatedDate;

    private LocalDateTime createdAt = LocalDateTime.now();

    public NewsArticle(String title, String newsLink, String description, LocalDateTime publicatedDate) {
        this.title = title;
        this.newsLink = newsLink;
        this.description = description;
        this.publicatedDate = publicatedDate;
    }
}
