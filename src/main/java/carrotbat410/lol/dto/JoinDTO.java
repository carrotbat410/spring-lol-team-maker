package carrotbat410.lol.dto;

import lombok.Getter;
import lombok.Setter;

//! JPA 기본생성자 필요(public or protected)
//TODO 모든 엔티티 생성자 주입으로 Setter줄이자.
@Setter
@Getter
public class JoinDTO {

    private String username;
    private String password;
}
