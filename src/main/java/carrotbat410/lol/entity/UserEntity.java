package carrotbat410.lol.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//TODO 모든 엔티티를 생성자를 통해서 Setter줄이자.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //TODO int Inteager중 어떤게 나은지 찾아보기?(범위, null등... 찾아보기)
    private String username;
    private String password;
    private String role;

    public UserEntity(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
