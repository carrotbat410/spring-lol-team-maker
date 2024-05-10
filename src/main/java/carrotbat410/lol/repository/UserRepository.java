package carrotbat410.lol.repository;

import carrotbat410.lol.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    Boolean existsByUsername(String username);

    //TODO Entity반환X
    User findByUsername(String username);
}
