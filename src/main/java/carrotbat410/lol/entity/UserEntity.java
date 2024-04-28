package carrotbat410.lol.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

//! JPA 기본생성자 필요(public or protected)
//TODO 모든 엔티티 생성자 주입으로 Setter줄이자.
@Entity
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //TODO int Inteager중 어떤게 나은지 찾아보기?(범위, null등... 찾아보기)

    private String username;
    private String password;

    private String role;
}
