package carrotbat410.lol.service.teamService;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import carrotbat410.lol.utils.RiotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GoldenBalanceModeAssignment implements TeamAssignmentStrategy {

    private final RiotUtils riotUtils;

    @Autowired
    public GoldenBalanceModeAssignment(RiotUtils riotUtils) {
        this.riotUtils = riotUtils;
    }

    @Override
    public TeamAssignResponseDTO assignTeams(TeamAssignRequestDTO requestDTO) {
        ArrayList<SummonerDTO> team1List = (ArrayList<SummonerDTO>) requestDTO.getTeam1List();
        ArrayList<SummonerDTO> team2List = (ArrayList<SummonerDTO>) requestDTO.getTeam2List();
        ArrayList<SummonerDTO> noTeamList = (ArrayList<SummonerDTO>) requestDTO.getNoTeamList();

        GoldenBalanceUtils result = new GoldenBalanceUtils(team1List, team2List, noTeamList);
        return new TeamAssignResponseDTO(result.bestTeam1List, result.bestTeam2List, riotUtils.mmrToString(result.bestTeam1AvgMmr), riotUtils.mmrToString(result.bestTeam2AvgMmr));
    }
}

