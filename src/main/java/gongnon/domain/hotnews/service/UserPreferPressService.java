package gongnon.domain.hotnews.service;

import gongnon.domain.hotnews.model.UserPreferPress;
import gongnon.domain.hotnews.model.PredefinedPress;
import gongnon.domain.hotnews.repository.UserPreferPressRepository;
import gongnon.domain.hotnews.repository.PredefinedPressRepository;
import gongnon.domain.appUser.model.AppUser;
import gongnon.domain.appUser.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserPreferPressService {
    @Autowired
    private UserPreferPressRepository userPreferPressRepository;

    @Autowired
    private PredefinedPressRepository predefinedPressRepository;

    @Autowired
    private AppUserRepository userRepository;

    // 사용자의 선호 언론사 변경
    @Transactional
    public UserPreferPress updateUserPreferPress(Long userId, Long newPressId) {
        // 사용자 확인
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(("존재하지 않는 사용자입니다")));

        // 새로운 언론사 확인
        PredefinedPress newPress = predefinedPressRepository.findById(newPressId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 언론사입니다."));

        // 기존 선호 언론사 삭제
        userPreferPressRepository.deleteByUser_Id(userId);

        // 새로운 언론사 저장
        UserPreferPress newUserPreferPress = new UserPreferPress();
        newUserPreferPress.setUser(user);
        newUserPreferPress.setPress(newPress);
        userPreferPressRepository.save(newUserPreferPress);

        // app_users 테이블에 preferPressId 동기화
        user.setPreferPress(newPress);
        userRepository.save(user);

        return userPreferPressRepository.save(newUserPreferPress);
    }

    // 사용자의 현재 선호 언론사 조회
    public UserPreferPress getUserPreferPress(Long userId) {
        return userPreferPressRepository.findByUser_Id(userId);
    }

    // 모든 언론사 목록 조회
    public List<PredefinedPress> getAllPress(){
        return predefinedPressRepository.findAll();
    }
}
