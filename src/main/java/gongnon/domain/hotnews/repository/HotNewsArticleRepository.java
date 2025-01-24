package gongnon.domain.hotnews.repository;

import gongnon.domain.hotnews.model.HotNewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotNewsArticleRepository extends JpaRepository<HotNewsArticle, Long> {
    // 특정 날짜 모든 뉴스 조회
    List<HotNewsArticle> findByNewsDate(LocalDate newsDate);

    // 특정 언론사 모든 뉴스 조회
    List<HotNewsArticle> findByPress_Id(Long pressId);

    // 특정 날짜 + 언론사 뉴스 조회
    List<HotNewsArticle> findByNewsDateAndPress_Id(LocalDate newsDate, Long pressId);

    // 특정 날짜 뉴스 삭제 -> 날짜별 최신화
    void deleteByNewsDate(LocalDate newsDate);
}
