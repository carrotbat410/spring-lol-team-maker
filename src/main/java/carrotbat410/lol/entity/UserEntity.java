package carrotbat410.lol.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter //TODO Setter들 나중에 다 찾아서 최대한 안쓰도록 바꾸기
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //TODO int Inteager중 어떤게 나은지 찾아보기?(범위, null등... 찾아보기)

    private String username;
    private String password;

    private String role;
}
