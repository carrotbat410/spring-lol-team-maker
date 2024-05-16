package carrotbat410.lol.service;

import carrotbat410.lol.dto.SummonerDTO;
import carrotbat410.lol.entity.Summoner;
import carrotbat410.lol.repository.SummonerRepository;
import carrotbat410.lol.utils.RiotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SummonerService {
    private final SummonerRepository summonerRepository;
    private final RiotUtils riotUtils;

    public List<SummonerDTO> getSummoners(Long userId) {
        List<Summoner> summoners = summonerRepository.findByUserId(userId);
        ArrayList<SummonerDTO> summonerDTOs = new ArrayList<>();
        for (Summoner summoner: summoners) {
            summonerDTOs.add(SummonerDTO.from(summoner));
        }
        return summonerDTOs;
    }

    public SummonerDTO addSummoner(String summonerName, String tagLine) {

        riotUtils.getSummoner(summonerName,tagLine);


        return new SummonerDTO(3L,"test","test");
    }


}