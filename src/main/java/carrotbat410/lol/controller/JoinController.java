package carrotbat410.lol.controller;

import carrotbat410.lol.dto.JoinDTO;
import carrotbat410.lol.service.JoinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join")
    public String joinProcess(@RequestBody @Validated JoinDTO joinDTO) {

        joinService.joinProcess(joinDTO);

        return "ok"; //TODO 응답 형태 공통화하기
    }
}
