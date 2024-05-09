package carrotbat410.lol.service;

import carrotbat410.lol.dto.JoinDTO;
import carrotbat410.lol.entity.UserEntity;
import carrotbat410.lol.exhandler.exception.AlreadyExistException;
import carrotbat410.lol.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        validateDuplicatedUser(username);

        UserEntity userEntity = new UserEntity(username, bCryptPasswordEncoder.encode(password), "ROLE_ADMIN"); //TODO ENUM으로 바꾸기, ROLE_USER경우 추가하기

        userRepository.save(userEntity);
    }

    private void validateDuplicatedUser(String username) {
        Boolean isExist = userRepository.existsByUsername(username);
        if (isExist) throw new AlreadyExistException("이미 존재하는 유저입니다.");
    }

}