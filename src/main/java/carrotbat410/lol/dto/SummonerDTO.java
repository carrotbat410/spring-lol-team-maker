package carrotbat410.lol.dto;

import carrotbat410.lol.entity.Summoner;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SummonerDTO {
    @Column(name = "summoner_id")
    private final Long id;
    private final String summonerName;
    private final String tagLine;

    public static SummonerDTO from(Summoner summoner) {
        return new SummonerDTO(summoner.getId(), summoner.getSummonerName(), summoner.getTagLine());
    }
}