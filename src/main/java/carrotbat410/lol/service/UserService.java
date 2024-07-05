package carrotbat410.lol.service;

import carrotbat410.lol.dto.auth.JoinDTO;
import carrotbat410.lol.entity.User;
import carrotbat410.lol.exhandler.exception.DataConflictException;
import carrotbat410.lol.repository.SummonerRepository;
import carrotbat410.lol.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final SummonerRepository summonerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       SummonerRepository summonerRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.summonerRepository = summonerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO) {
        validateDuplicatedUser(joinDTO.getUsername());
        saveUser(joinDTO);
    }

    @Transactional(readOnly = true)
    public void validateDuplicatedUser(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new DataConflictException("이미 존재하는 유저입니다.");
        }
    }

    @Transactional
    public void saveUser(JoinDTO joinDTO) {
        User user = new User(null, joinDTO.getUsername(), bCryptPasswordEncoder.encode(joinDTO.getPassword()), "ROLE_USER");
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        summonerRepository.deleteByUserId(id);
    }
}