package carrotbat410.lol.dto;

import carrotbat410.lol.entity.Summoner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SummonerDTO {
    private final String summonerName;
    private final String tagLine;
    private final String tier;
    private final String rank;
    private final int level;
    private final int wins;
    private final int losses;
    private final int iconId;


    public static SummonerDTO from(Summoner summoner) {
        return new SummonerDTO(summoner.getSummonerName(),
                summoner.getTagLine(), summoner.getTier(), summoner.getRank1(),
                summoner.getLevel(), summoner.getWins(), summoner.getLosses(),summoner.getIconId());
    }

    public static SummonerDTO of(String summonerName, String tagLine, String tier, String rank,
                                 int level, int wins, int losses, int iconId) {
        return new SummonerDTO(summonerName, tagLine, tier, rank, level, wins, losses, iconId);
    }
}