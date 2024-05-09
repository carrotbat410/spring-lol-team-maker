package carrotbat410.lol.controller;

import carrotbat410.lol.dto.JoinDTO;
import carrotbat410.lol.dto.SuccessResult;
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
    public SuccessResult joinProcess(@RequestBody @Validated JoinDTO joinDTO) {

        joinService.joinProcess(joinDTO);

        return new SuccessResult("ok");
    }
}
