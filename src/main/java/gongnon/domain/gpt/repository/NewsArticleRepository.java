package gongnon.domain.gpt.repository;

import gongnon.domain.gpt.model.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long>{
}
