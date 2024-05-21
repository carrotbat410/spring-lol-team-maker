package carrotbat410.lol.dto.summoner;

import carrotbat410.lol.dto.riot.SummonerApiTotalDTO;
import carrotbat410.lol.entity.Summoner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private final String updatedAt;

    public static SummonerDTO from(Summoner summoner) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedUpdatedAt = summoner.getUpdatedAt().format(formatter);

        return new SummonerDTO(
                summoner.getId(), summoner.getSummonerName(), summoner.getTagLine(), summoner.getTier(), summoner.getRank1(),
                summoner.getMmr(), summoner.getLevel(), summoner.getWins(), summoner.getLosses(),summoner.getIconId(), formattedUpdatedAt);
    }

}