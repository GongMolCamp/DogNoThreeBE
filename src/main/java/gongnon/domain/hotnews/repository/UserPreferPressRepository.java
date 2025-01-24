package gongnon.domain.hotnews.repository;

import gongnon.domain.hotnews.model.UserPreferPress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferPressRepository extends JpaRepository<UserPreferPress, Long> {
    UserPreferPress findByUser_Id(Long userId);

    boolean existsByUser_IdAndPress_Id(Long userId, Long pressId);

    void deleteByUser_Id(Long userId);
}