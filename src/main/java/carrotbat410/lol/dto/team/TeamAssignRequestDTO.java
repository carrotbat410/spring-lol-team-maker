package carrotbat410.lol.dto.team;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamAssignRequestDTO {

    private TeamAssignMode assingMode; //TODO ENUM 타입으로?

    private SummonerDTO[] team1List;
    private SummonerDTO[] team2List;
    private SummonerDTO[] noTeamList;

}
