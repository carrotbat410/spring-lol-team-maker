package carrotbat410.lol.repository;

import carrotbat410.lol.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByUsername(String username);

    //TODO Entity반환X
    UserEntity findByUsername(String username);
}
