package carrotbat410.lol.controller;

import carrotbat410.lol.dto.JoinDTO;
import carrotbat410.lol.service.JoinService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO) {

        if(joinDTO.getUsername() == null || joinDTO.getPassword() == null) return "아이디 또는 패스워드를 확인해주세요";
        joinService.joinProcess(joinDTO);

        return "ok";
    }
}
