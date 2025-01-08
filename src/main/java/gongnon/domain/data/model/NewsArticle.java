package gongnon.domain.data.model;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String link;

    @Column(length = 1000)
    private String description;

    private LocalDateTime pubDate;

    private LocalDateTime createdDate = LocalDateTime.now();

    public NewsArticle(String title, String link, String description, LocalDateTime pubDate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
    }
}
