package carrotbat410.lol.controller;

import carrotbat410.lol.dto.auth.JoinDTO;
import carrotbat410.lol.dto.result.SuccessResult;
import carrotbat410.lol.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public SuccessResult joinProcess(@RequestBody @Validated JoinDTO joinDTO) {
        userService.joinProcess(joinDTO);

        return new SuccessResult("ok");
    }
}
