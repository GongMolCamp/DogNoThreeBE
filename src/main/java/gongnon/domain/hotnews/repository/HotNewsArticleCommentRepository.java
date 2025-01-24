package gongnon.domain.hotnews.repository;

import gongnon.domain.hotnews.model.HotNewsArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotNewsArticleCommentRepository extends JpaRepository<HotNewsArticleComment, Long> {
}
