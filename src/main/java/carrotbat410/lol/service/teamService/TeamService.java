package carrotbat410.lol.service.teamService;

import carrotbat410.lol.dto.team.TeamAssignMode;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static carrotbat410.lol.dto.team.TeamAssignMode.*;


@Service
public class TeamService {

    private final Map<TeamAssignMode, TeamAssignmentStrategy> strategies;

    @Autowired
    public TeamService(List<TeamAssignmentStrategy> strategyList) {
        strategies = strategyList.stream().collect(Collectors.toMap(
                strategy -> strategy instanceof RandomModeAssignment ? RANDOM :
                        strategy instanceof BalanceModeAssignment ? BALANCE :
                                strategy instanceof GoldenBalanceModeAssignment ? GOLDEN_BALANCE : null,
                strategy -> strategy));
    }

    public TeamAssignResponseDTO executeAssignTeams(TeamAssignRequestDTO requestDTO) {
        TeamAssignMode mode = requestDTO.getAssingMode();
        TeamAssignmentStrategy strategy = strategies.get(mode);
        if (strategy == null) throw new IllegalArgumentException("제공하지 않는 모드입니다.");
        return strategy.assignTeams(requestDTO);
    }
}
