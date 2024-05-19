package carrotbat410.lol.controller;

import carrotbat410.lol.dto.auth.CustomUserDetails;
import carrotbat410.lol.dto.result.SuccessResult;
import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.summoner.AddSummonerReqeustDTO;
import carrotbat410.lol.service.SummonerService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public SuccessResult<SummonerDTO> addSummoner(@RequestBody @Validated AddSummonerReqeustDTO addSummonerReqeustDTO) {

        String summonerName = addSummonerReqeustDTO.getSummonerName();
        String tagLine = addSummonerReqeustDTO.getTagLine();
        if(StringUtils.isBlank(tagLine)) tagLine = "KR1";

        //TODO userId값 토큰에서 가져오기 (프론트에서 userId전달해서 받으면 안됨. 누가 프론트에서 조작해서 넘길 수 있으니?)
        SummonerDTO addedSummoner = summonerService.addSummoner(1L, summonerName, tagLine);
//        SummonerDTO addedSummoner = summonerService.addSummoner(1L, "통티모바베큐", "KR1"); // 솔랭 팀랭 모두 언랭인경우
//        SummonerDTO addedSummoner1 = summonerService.addSummoner(1L, "E크에크파이크", "KR1"); // 솔랭
//        SummonerDTO addedSummoner2 = summonerService.addSummoner(1L, "cikcik", "KR1"); // 솔랭 (챌린저)
//        SummonerDTO addedSummoner3 = summonerService.addSummoner(1L, "범코야끼", "KR1"); // 솔랭 (그마)
//        SummonerDTO addedSummoner4 = summonerService.addSummoner(1L, "근성맨", "KR1"); // 솔랭 (마스터)
//        SummonerDTO addedSummoner5 = summonerService.addSummoner(1L, "오잉앵", "KR1"); // 솔랭, 팀랭
        return new SuccessResult<>("ok", addedSummoner);
    }


    private Long extractUserIdFromAuthentication(Authentication authentication) {
        // Authentication 객체에서 사용자 ID를 추출하는 로직
        // CustomUserDetails를 사용하여 사용자 ID를 추출할 수 있음.
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

}
