package carrotbat410.lol.controller;

import carrotbat410.lol.dto.auth.CustomUserDetails;
import carrotbat410.lol.dto.result.SuccessResult;
import carrotbat410.lol.dto.SummonerDTO;
import carrotbat410.lol.service.SummonerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SummonerController {

    private final SummonerService summonerService;

    @GetMapping("/summoners") //! 복수 주의
    public SuccessResult<List<SummonerDTO>> getSummoners() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = extractUserIdFromAuthentication(authentication);
        List<SummonerDTO> summonerDTOs = summonerService.getSummoners(userId);

        return new SuccessResult<>("ok", summonerDTOs, summonerDTOs.size());
    }

    @PostMapping("/summoner")
    public SuccessResult<SummonerDTO> addSummoner() {
        //TODO body로 받기, validation까지
//        SummonerDTO addedSummoner = summonerService.addSummoner("E크에크파이크", "KR1"); // x
//        SummonerDTO addedSummoner = summonerService.addSummoner("의심하지말고해", "KR1"); // 솔랭만
        SummonerDTO addedSummoner = summonerService.addSummoner("오잉앵", "KR1"); // 솔랭, 팀랭
        return new SuccessResult<>("ok", addedSummoner);
    }


    private Long extractUserIdFromAuthentication(Authentication authentication) {
        // Authentication 객체에서 사용자 ID를 추출하는 로직
        // CustomUserDetails를 사용하여 사용자 ID를 추출할 수 있음.
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

}
