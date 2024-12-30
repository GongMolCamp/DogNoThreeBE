package gongnon.domain.news.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class NewsArticle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String link;

	@Column(length = 1000)
	private String description;

	private LocalDateTime pubDate;

	// 추가 필드: 예를 들어, 저장 일자
	private LocalDateTime createdDate = LocalDateTime.now();
}
