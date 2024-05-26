package carrotbat410.lol.service;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import org.springframework.stereotype.Service;


@Service
public class TeamService {

    public TeamAssignResponseDTO makeResultWithRandomMode(TeamAssignRequestDTO requestDTO) {
        SummonerDTO[] finalTeam1List = requestDTO.getTeam1List();
        SummonerDTO[] finalTeam2List = requestDTO.getTeam2List();

        int requiredTeam1Cnt = 5 - requestDTO.getTeam1List().length;
        for(int i = 0; i < requiredTeam1Cnt; i++) {

        }

        return new TeamAssignResponseDTO(finalTeam1List, finalTeam2List, "GOLD 2", "GOLD 2");
    }

    public TeamAssignResponseDTO makeResultWithBalanceMode(TeamAssignRequestDTO requestDTO) {
        return new TeamAssignResponseDTO();
    }
    public TeamAssignResponseDTO makeResultWithGoldenBalanceMode(TeamAssignRequestDTO requestDTO) {
        return new TeamAssignResponseDTO();
    }
}
