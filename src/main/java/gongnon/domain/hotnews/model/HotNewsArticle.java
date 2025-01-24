package gongnon.domain.hotnews.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hotnews_articles")
public class HotNewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false)
    private String link;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private int ranking;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "press_id", nullable = false)
    private PredefinedPress press;

    @Column(nullable = false)
    private LocalDate newsDate; // 뉴스를 수집한 날짜 (매일의 탑5 관리를 위한 기준)

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotNewsArticleComment> comments;
}


