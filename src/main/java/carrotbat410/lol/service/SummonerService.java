package carrotbat410.lol.service;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.entity.Summoner;
import carrotbat410.lol.exhandler.exception.AlreadyExistException;
import carrotbat410.lol.repository.SummonerRepository;
import carrotbat410.lol.utils.RiotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummonerService {
    private final SummonerRepository summonerRepository;
    private final RiotUtils riotUtils;

//    public List<SummonerDTO> getSummoners(Long userId) {
//        List<Summoner> summoners = summonerRepository.findByUserId(userId);
//        ArrayList<SummonerDTO> summonerDTOs = new ArrayList<>();
//        for (Summoner summoner: summoners) {
//            summonerDTOs.add(SummonerDTO.from(summoner));
//        }
//        return summonerDTOs;
//    }

    public SummonerDTO addSummoner(Long userId, String summonerName, String tagLine) {

        Summoner existingAddedSummoner = summonerRepository.findFirstByUserIdAndSummonerNameAndTagLine(userId, summonerName, tagLine);

        if(existingAddedSummoner != null) throw new AlreadyExistException("이미 존재하는 유저입니다.");

        SummonerDTO summonerDTO = riotUtils.getSummoner(summonerName, tagLine);

        Summoner summoner = Summoner.of(userId, summonerDTO.getSummonerName(), summonerDTO.getTagLine(), summonerDTO.getTier(), summonerDTO.getRank(),
                summonerDTO.getMmr(), summonerDTO.getLevel(), summonerDTO.getWins(), summonerDTO.getLosses(), summonerDTO.getIconId());

        summonerRepository.save(summoner);

        return summonerDTO;
    }


}