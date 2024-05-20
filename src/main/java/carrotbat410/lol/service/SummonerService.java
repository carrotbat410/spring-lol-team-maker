package carrotbat410.lol.service;

import carrotbat410.lol.dto.riot.SummonerApiTotalDTO;
import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.entity.Summoner;
import carrotbat410.lol.exhandler.exception.AlreadyExistException;
import carrotbat410.lol.exhandler.exception.NotFoundException;
import carrotbat410.lol.repository.SummonerRepository;
import carrotbat410.lol.utils.RiotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public SummonerDTO addSummoner(Long userId, String summonerName, String tagLine) {

        Summoner existingAddedSummoner = summonerRepository.findFirstByUserIdAndSummonerNameAndTagLine(userId, summonerName, tagLine);

        if(existingAddedSummoner != null) throw new AlreadyExistException("이미 존재하는 유저입니다.");

        SummonerApiTotalDTO apiResult = riotUtils.getSummoner(summonerName, tagLine);

        Summoner summoner = Summoner.of(userId, apiResult.getSummonerName(), apiResult.getTagLine(), apiResult.getTier(), apiResult.getRank(),
                apiResult.getMmr(), apiResult.getLevel(), apiResult.getWins(), apiResult.getLosses(), apiResult.getIconId());

        Summoner saveResult = summonerRepository.save(summoner);

        return SummonerDTO.from(saveResult);
    }

//    public void updateSummoner(Long summonerId) {
//
//        Optional<Summoner> summoner = summonerRepository.findById(summonerId);
//
//        if(summoner.isEmpty()) throw new NotFoundException("해당 유저는 존재하지 않습니다.");
//
//        SummonerDTO summonerDTO = riotUtils.getSummoner(summoner.get().getSummonerName(), summoner.get().getTagLine());
//
////        riotUtils.getSummoner()
//    }

    public void deleteSummoner(Long summonerId) {
        summonerRepository.deleteById(summonerId);
    }


}