package carrotbat410.lol.service.teamService;

import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;

public interface TeamAssignmentStrategy {
    TeamAssignResponseDTO assignTeams(TeamAssignRequestDTO requestDTO);

}
