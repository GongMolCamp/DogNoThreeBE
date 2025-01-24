package gongnon.domain.news.repository;

import gongnon.domain.news.model.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//
@Repository
public interface NewsRepository extends JpaRepository<NewsArticle, Long> {
}
