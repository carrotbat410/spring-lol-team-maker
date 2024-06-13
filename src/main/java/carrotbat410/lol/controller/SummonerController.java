package carrotbat410.lol.controller;

import carrotbat410.lol.dto.auth.CustomUserDetails;
import carrotbat410.lol.dto.result.SuccessResult;
import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.summoner.AddSummonerReqeustDTO;
import carrotbat410.lol.exhandler.exception.DataConflictException;
import carrotbat410.lol.service.SummonerService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SummonerController {

    private final SummonerService summonerService;

    @GetMapping("/summoners") //! 복수 주의
    public SuccessResult<List<SummonerDTO>> getSummoners(Pageable pageable) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = extractUserIdFromAuthentication(authentication);

        List<SummonerDTO> summoners = summonerService.getSummoners(userId, pageable);

        return new SuccessResult<>("ok", summoners, summoners.size());
    }

    @PostMapping("/summoner")
    public SuccessResult<SummonerDTO> addSummoner(@RequestBody @Validated AddSummonerReqeustDTO addSummonerReqeustDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = extractUserIdFromAuthentication(authentication);

        int addedSummonerCnt = summonerService.getAddedSummonerCnt(userId);
        if(addedSummonerCnt >= 30) throw new DataConflictException("추가할 수 있는 최대 인원은 30명입니다.");

        String summonerName = addSummonerReqeustDTO.getSummonerName();
        String tagLine = addSummonerReqeustDTO.getTagLine();
        if(StringUtils.isBlank(tagLine)) tagLine = "KR1";

        SummonerDTO addedSummoner = summonerService.addSummoner(userId, summonerName, tagLine);

        return new SuccessResult<>("ok", addedSummoner);
    }

    @PatchMapping("/summoner/{summonerId}")
    public SuccessResult<SummonerDTO> updateSummoner(@PathVariable("summonerId") Long summonerId) {
        SummonerDTO summonerDTO = summonerService.updateSummoner(summonerId);
        return new SuccessResult<>("ok", summonerDTO);
    }

    @DeleteMapping("/summoner/{summonerId}")
    public SuccessResult deleteSummoner(@PathVariable("summonerId") Long summonerId) {
        summonerService.deleteSummoner(summonerId);
        return new SuccessResult<>("ok");
    }


    private Long extractUserIdFromAuthentication(Authentication authentication) {
        // Authentication 객체에서 사용자 ID를 추출하는 로직
        // CustomUserDetails를 사용하여 사용자 ID를 추출할 수 있음.
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

}
