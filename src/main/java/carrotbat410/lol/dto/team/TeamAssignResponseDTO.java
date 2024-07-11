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
    //* Request Response 의 타입을 인터페이스인 List로 선언하니 훨 나은듯
    private List<SummonerDTO> team1List;
    private List<SummonerDTO> team2List;
    private String team1AvgMmr;
    private String team2AvgMmr;
}
