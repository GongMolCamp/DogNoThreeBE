package gongnon.domain.news.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import gongnon.domain.news.model.NewsArticle;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
}
