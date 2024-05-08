package carrotbat410.lol.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

//Entity에 비해,  DTO는 Setter 맘껏 써도됨,
//@RequestBody->DTO mapping떄 Setter필요함.
@Setter
@Getter
public class JoinDTO {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String username;

    @NotBlank(message = "패스워드를 입력해주세요")
    private String password;
}
