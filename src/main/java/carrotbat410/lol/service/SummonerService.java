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
@Transactional //! transactional 추가해야 updateSummoner 적용됨. 원인 정확히 알아보기 (추측: 영속성 컨텍스트 생명주기가 tx와 같아서?) //TODO 외부 API요청 로직떄문에 가능하다면 trx안붙이는게 성능상 유리하지 않을까?
public class SummonerService {
    private final SummonerRepository summonerRepository;
    private final RiotUtils riotUtils;

    public List<SummonerDTO> getSummoners(Long userId, Pageable pageable) {
        return summonerRepository.findMySummoners(userId, pageable);
    }

    public SummonerDTO addSummoner(Long userId, String summonerName, String tagLine) {

        Summoner existingAddedSummoner = summonerRepository.findExistingSummoner(userId, summonerName, tagLine);
        if(existingAddedSummoner != null) throw new DataConflictException("이미 존재하는 유저입니다.");

        int addedSummonerCnt = getAddedSummonerCnt(userId);
        if(addedSummonerCnt >= 30) throw new DataConflictException("추가할 수 있는 최대 인원은 30명입니다.");

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

    public void deleteSummoner(Long summonerId) {
        summonerRepository.deleteById(summonerId);
    }

    private int getAddedSummonerCnt(Long userid) {
        return summonerRepository.countByUserId(userid);
    }


}