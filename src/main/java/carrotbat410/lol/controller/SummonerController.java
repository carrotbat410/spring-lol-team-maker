package carrotbat410.lol.controller;

import carrotbat410.lol.dto.CustomUserDetails;
import carrotbat410.lol.dto.SuccessResult;
import carrotbat410.lol.dto.SummonerDTO;
import carrotbat410.lol.entity.Summoner;
import carrotbat410.lol.service.SummonerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SummonerController {

    private final SummonerService summonerService;

    @GetMapping("/summoners")
    public SuccessResult<List<SummonerDTO>> getSummoners() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = extractUserIdFromAuthentication(authentication);
        List<SummonerDTO> summonerDTOs = summonerService.getSummoners(userId);

        return new SuccessResult<>("ok", summonerDTOs);
    }


    private Long extractUserIdFromAuthentication(Authentication authentication) {
        // Authentication 객체에서 사용자 ID를 추출하는 로직
        // CustomUserDetails를 사용하여 사용자 ID를 추출할 수 있음.
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

}
