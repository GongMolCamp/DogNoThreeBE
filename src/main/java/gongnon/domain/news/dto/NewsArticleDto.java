package gongnon.domain.news.dto;

import lombok.Builder;

@Builder
public record NewsArticleDto(
	String title,
	String description
) {
}
