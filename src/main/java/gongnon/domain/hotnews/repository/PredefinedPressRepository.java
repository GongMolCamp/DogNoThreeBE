package gongnon.domain.hotnews.repository;

import gongnon.domain.hotnews.model.PredefinedPress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredefinedPressRepository extends JpaRepository<PredefinedPress, Long> {
    // 언론사 이름으로 조회
    PredefinedPress findByName(String name);
}