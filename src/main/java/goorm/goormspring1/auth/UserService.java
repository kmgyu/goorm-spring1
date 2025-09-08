package goorm.goormspring1.auth;

import goorm.goormspring1.auth.dto.LoginDTO;
import goorm.goormspring1.auth.dto.ProfileUpdateDTO;
import goorm.goormspring1.auth.dto.SignupDTO;
import goorm.goormspring1.auth.exception.DuplicateEmailException;
import goorm.goormspring1.auth.exception.InvalidCredentialsException;
import goorm.goormspring1.auth.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User signup(SignupDTO signupDTO) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(signupDTO.getEmail())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 확인 검사
        if (!signupDTO.getPassword().equals(signupDTO.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 사용자 엔티티 생성
        User user = new User();
        user.setEmail(signupDTO.getEmail());
        user.setPassword(signupDTO.getPassword()); // 평문 저장 (추후 암호화 예정)
        user.setNickname(signupDTO.getNickname());

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User authenticate(LoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        User user = userOptional.get();

        // 평문 비밀번호 검증 (추후 암호화 예정)
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

    public User updateProfile(Long userId, ProfileUpdateDTO profileUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인 (평문 비교)
        if (!user.getPassword().equals(profileUpdateDTO.getCurrentPassword())) {
            throw new InvalidCredentialsException("현재 비밀번호가 일치하지 않습니다.");
        }


        // 닉네임 업데이트
        user.setNickname(profileUpdateDTO.getNickname());

        // 새 비밀번호가 입력된 경우 비밀번호 변경
        if (profileUpdateDTO.getNewPassword() != null && !profileUpdateDTO.getNewPassword().isEmpty()) {
            // 새 비밀번호 확인 검사
            if (!profileUpdateDTO.getNewPassword().equals(profileUpdateDTO.getNewPasswordConfirm())) {
                throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
            }

            // 평문으로 저장 (추후 암호화 예정)
            user.setPassword(profileUpdateDTO.getNewPassword());
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
