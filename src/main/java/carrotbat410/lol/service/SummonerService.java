package carrotbat410.lol.service;

import carrotbat410.lol.dto.riot.SummonerApiTotalDTO;
import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.entity.Summoner;
import carrotbat410.lol.exhandler.exception.DataConflictException;
import carrotbat410.lol.exhandler.exception.NotFoundException;
import carrotbat410.lol.repository.SummonerRepository;
import carrotbat410.lol.utils.RiotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional //! transactional 추가해야 updateSummoner 적용됨. 원인 정확히 알아보기 (추측: 영속성 컨텍스트 생명주기가 tx와 같아서?)
public class SummonerService {
    private final SummonerRepository summonerRepository;
    private final RiotUtils riotUtils;

    public List<SummonerDTO> getSummoners(Long userId, Pageable pageable) {
        return summonerRepository.findMySummoners(userId, pageable);
    }

    public SummonerDTO addSummoner(Long userId, String summonerName, String tagLine) {

        //TODO 묵직한큰주먹 추가하고, 묵직한 큰주먹 추가해도 지금 되고있음. 자체 db검증할떄는 띄어쓰기 제거하기 중복되는거 있는지 체크해야함. + 대소문자 차이없이 검색해야함.
        // test case1. 묵직한큰주먹, 묵직한 큰주먹, 묵직한 큰 주먹.   의문인 점은 Riot api요청시 Akaps, Aka ps는 안되네?
        // test case2. 바로 Mute All, 바로 Mute all, 바로 mute all
        // test case3. Mute all Ignore
        Summoner existingAddedSummoner = summonerRepository.findFirstByUserIdAndSummonerNameAndTagLine(userId, summonerName, tagLine);

        if(existingAddedSummoner != null) throw new DataConflictException("이미 존재하는 유저입니다.");

        SummonerApiTotalDTO apiResult = riotUtils.getSummoner(summonerName, tagLine);

        Summoner summoner = Summoner.of(userId, apiResult.getSummonerName(), apiResult.getTagLine(), apiResult.getTier(), apiResult.getRank(),
                apiResult.getMmr(), apiResult.getLevel(), apiResult.getWins(), apiResult.getLosses(), apiResult.getIconId());

        Summoner saveResult = summonerRepository.save(summoner);

        return SummonerDTO.from(saveResult);
    }

    public SummonerDTO updateSummoner(Long summonerId) {

        Summoner summoner = summonerRepository.findById(summonerId).orElseThrow(() -> new NotFoundException("해당 유저는 존재하지 않습니다."));

        SummonerApiTotalDTO apiResult = riotUtils.getSummoner(summoner.getSummonerName(), summoner.getTagLine());

        summoner.updateSummoner(apiResult);

        summoner.updateTimestamp(); // 최종적으로 update쿼리는 한번만 나간다(JPA는 commit시점에 한번에 최적화하여 호출하니까)

        return SummonerDTO.from(summoner);
    }

    public int getAddedSummonerCnt(Long userid) {
        return summonerRepository.countByUserId(userid);
    }

    public void deleteSummoner(Long summonerId) {
        summonerRepository.deleteById(summonerId);
    }


}