package gongnon.domain.sms.repository;

import gongnon.domain.sms.model.Message;
import gongnon.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SmsRepository extends JpaRepository<Message, Long> {
	List<Message> findByUser(User user);
}
