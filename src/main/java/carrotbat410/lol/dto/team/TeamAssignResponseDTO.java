package carrotbat410.lol.dto.team;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

//@RequiredArgsConstructor
@NoArgsConstructor //TODO RequiredArgsConstructor 사용하기
@AllArgsConstructor //TODO RequiredArgsConstructor 사용하기
public class TeamAssignResponseDTO {
    private SummonerDTO[] team1List;
    private SummonerDTO[] team2List;
    private String team1AvgMmr;
    private String team2AvgMmr;
}
