package gongnon.domain.data.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewsResponseDto {
    private List<NewsArticleDto> articles;
}
