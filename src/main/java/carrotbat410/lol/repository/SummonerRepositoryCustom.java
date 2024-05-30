package carrotbat410.lol.repository;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.entity.Summoner;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SummonerRepositoryCustom {
    List<SummonerDTO> findMySummoners(Long userId, Pageable pageable);

    Summoner findExistingSummoner(Long userId, String summonerName, String tagLine);
}
