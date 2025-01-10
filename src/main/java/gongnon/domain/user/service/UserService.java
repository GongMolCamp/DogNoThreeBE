package gongnon.domain.user.service;

import gongnon.domain.user.model.User;
import gongnon.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public void registerUser(User user) {
		// 비밀번호 암호화
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}

	public Optional<User> loginUser(String username, String rawPassword) {
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isPresent() && passwordEncoder.matches(rawPassword, user.get().getPassword())) {
			return user;
		}
		return Optional.empty();
	}
}
