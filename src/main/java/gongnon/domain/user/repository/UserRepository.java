package gongnon.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import gongnon.domain.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
}
