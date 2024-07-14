package carrotbat410.lol.dto.riot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class RiotAccountApiResponseDTO {
    private String puuid;
    private String gameName;
    private String tagLine;
}