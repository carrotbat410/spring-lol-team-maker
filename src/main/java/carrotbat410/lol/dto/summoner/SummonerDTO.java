package carrotbat410.lol.dto.summoner;

import carrotbat410.lol.dto.riot.SummonerApiTotalDTO;
import carrotbat410.lol.entity.Summoner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SummonerDTO {
    private final Long id;
    private final String summonerName;
    private final String tagLine;
    private final String tier;
    private final Integer rank;
    private final int mmr;
    private final int level;
    private final int wins;
    private final int losses;
    private final int iconId;


    public static SummonerDTO from(Summoner summoner) {
        return new SummonerDTO(
                summoner.getId(), summoner.getSummonerName(), summoner.getTagLine(), summoner.getTier(), summoner.getRank1(),
                summoner.getMmr(), summoner.getLevel(), summoner.getWins(), summoner.getLosses(),summoner.getIconId());
    }

    public static SummonerDTO from(SummonerApiTotalDTO summonerApiTotalDTO, Long summonerId) {
        return new SummonerDTO(summonerId, summonerApiTotalDTO.getSummonerName(), summonerApiTotalDTO.getTagLine(), summonerApiTotalDTO.getTier(), summonerApiTotalDTO.getRank(),
                summonerApiTotalDTO.getMmr(), summonerApiTotalDTO.getLevel(), summonerApiTotalDTO.getWins(), summonerApiTotalDTO.getLosses(), summonerApiTotalDTO.getIconId());
    }

}