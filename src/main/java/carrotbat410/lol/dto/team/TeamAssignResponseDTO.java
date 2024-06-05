package carrotbat410.lol.dto.team;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

//@RequiredArgsConstructor
@NoArgsConstructor //TODO RequiredArgsConstructor 사용하기
@AllArgsConstructor //TODO RequiredArgsConstructor 사용하기
@ToString
@Getter
public class TeamAssignResponseDTO {
    //TODO 타입 SummonerDTO[]으로 통일시키고 싶음.
    private ArrayList<SummonerDTO> team1List;
    private ArrayList<SummonerDTO> team2List;
    private String team1AvgMmr;
    private String team2AvgMmr;
}
