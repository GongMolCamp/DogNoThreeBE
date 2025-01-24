package gongnon.domain.user.service;

import gongnon.domain.user.model.User;
import gongnon.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public void registerUser(User user) {
		// 같은 전화번호 혹은 username의 사용자가 이미 존재하는지 확인
		if (userRepository.findByUsername(user.getUsername()).isPresent() || userRepository.findByPhone(user.getPhone()).isPresent()) {
			throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
		}

		// 비밀번호 암호화
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}

	// 로그인 로직 (아이디+비번 체크)
	public Optional<User> loginUser(String username, String rawPassword) {
		// DB에서 username으로 조회
		Optional<User> userOpt = userRepository.findByUsername(username);
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			// passwordEncoder.matches(사용자가 입력한 비번, DB에 저장된 해시)
			if (passwordEncoder.matches(rawPassword, user.getPassword())) {
				return Optional.of(user);
			}
		}
		return Optional.empty();
	}


	// 사용자 정보 수정 (DB update)
	public void updateUser(User user) {
		userRepository.save(user);
		// user.getId()가 이미 있으므로, JPA가 update 쿼리 수행
	}


	//추가해본거
	// 모든 사용자 조회
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}
	// 특정 사용자 조회
	public Optional<User> findUserById(Long id) {
		return userRepository.findById(id);
	}
}
