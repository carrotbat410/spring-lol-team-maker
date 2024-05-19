package carrotbat410.lol.dto.riot;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SummonerApiResponseDTO {
    private String id;
    private int profileIconId;
    private int summonerLevel;
}