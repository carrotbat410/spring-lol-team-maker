package carrotbat410.lol.dto;

import lombok.Getter;
import lombok.Setter;

//Entity에 비해,  DTO는 Setter 맘껏 써도됨,
//@RequestBody->DTO mapping떄 Setter필요함.
@Setter
@Getter
public class JoinDTO {

    private String username;
    private String password;
}
