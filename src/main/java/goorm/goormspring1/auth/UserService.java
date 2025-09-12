package goorm.goormspring1.auth;

import goorm.goormspring1.auth.dto.LoginDTO;
import goorm.goormspring1.auth.dto.ProfileUpdateDTO;
import goorm.goormspring1.auth.dto.SignupDTO;
import goorm.goormspring1.auth.exception.DuplicateEmailException;
import goorm.goormspring1.auth.exception.InvalidCredentialsException;
import goorm.goormspring1.auth.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Spring Security loadUserByUsername 호출: email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Spring Security - 사용자 없음: email={}", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });

        log.debug("Spring Security - 사용자 로드 성공: email={}, authorities={}",
                email, user.getAuthorities());
        return user;
    }

//    // 회원가입 시 비밀번호 암호화
//    public void signup(SignupDto signupDto) {
//        String encodedPassword = passwordEncoder.encode(signupDto.getPassword());
//        // User 생성 및 저장...
//    }

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
        String encodedPassword = passwordEncoder.encode(signupDTO.getPassword());
        user.setPassword(encodedPassword); // 평문 저장 (추후 암호화 예정)
        user.setNickname(signupDTO.getNickname());

        User savedUser = userRepository.save(user);
        log.info("회원가입 성공: email={}, userId={}", signupDTO.getEmail(), savedUser.getId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User authenticate(LoginDTO loginDTO) {
        log.info("로그인 시도: email={}", loginDTO.getEmail());

        Optional<User> userOptional = userRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isEmpty()) {
            log.warn("로그인 실패 - 사용자 없음: email={}", loginDTO.getEmail());
            throw new InvalidCredentialsException(loginDTO.getEmail());
        }

        User user = userOptional.get();
        log.debug("사용자 찾음: email={}, storedPasswordLength={}", user.getEmail(), user.getPassword().length());

        // BCrypt 암호화된 비밀번호 검증
        boolean passwordMatches = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        log.debug("비밀번호 검증 결과: email={}, matches={}", loginDTO.getEmail(), passwordMatches);

        if (!passwordMatches) {
            log.warn("로그인 실패 - 비밀번호 불일치: email={}", loginDTO.getEmail());
            throw new InvalidCredentialsException(loginDTO.getEmail());
        }

        log.info("로그인 성공: email={}, userId={}", loginDTO.getEmail(), user.getId());
        return user;
    }

    public User updateProfile(Long userId, ProfileUpdateDTO profileUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 현재 비밀번호 확인 (BCrypt 비교)
        if (!passwordEncoder.matches(profileUpdateDTO.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException(user.getEmail());
        }

        // 닉네임 업데이트
        user.setNickname(profileUpdateDTO.getNickname());

        // 새 비밀번호가 입력된 경우 비밀번호 변경
        if (profileUpdateDTO.getNewPassword() != null && !profileUpdateDTO.getNewPassword().isEmpty()) {
            // 새 비밀번호 확인 검사
            if (!profileUpdateDTO.getNewPassword().equals(profileUpdateDTO.getNewPasswordConfirm())) {
                throw new IllegalArgumentException("Password mismatch");
            }

            // BCrypt로 암호화하여 저장
            user.setPassword(passwordEncoder.encode(profileUpdateDTO.getNewPassword()));
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
