package carrotbat410.lol.service;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class TeamService {

    public TeamAssignResponseDTO makeResultWithRandomMode(TeamAssignRequestDTO requestDTO) {
        ArrayList<SummonerDTO> finalTeam1List = new ArrayList<>();
        ArrayList<SummonerDTO> finalTeam2List = new ArrayList<>();

        for (SummonerDTO summonerDTO : finalTeam1List) finalTeam1List.add(summonerDTO);
        for (SummonerDTO summonerDTO : finalTeam2List) finalTeam2List.add(summonerDTO);

        int requiredTeam1Cnt = 5 - requestDTO.getTeam1List().length;

        for(int i = 0; i < requiredTeam1Cnt; i++) {

            //TODO 카드 덱 중 3장 뽑기 로직
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
