package carrotbat410.lol.controller;


import carrotbat410.lol.dto.result.SuccessResult;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import carrotbat410.lol.service.teamService.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static carrotbat410.lol.dto.team.TeamAssignMode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/team")
    public SuccessResult<TeamAssignResponseDTO> makeTeamResult(@RequestBody @Validated TeamAssignRequestDTO requestDTO) {

        validateRequest(requestDTO);

        if (requestDTO.getAssingMode() == null) requestDTO.setAssingMode(RANDOM);

        TeamAssignResponseDTO result = teamService.executeAssignTeams(requestDTO);

        return new SuccessResult<>("ok", result);
    }

    private void validateRequest(TeamAssignRequestDTO requestDTO) {
        //TODO requestDTO.getAssingMode() == null이면 에러 나도록 수정하기
        int totalSummonerSize = requestDTO.getTeam1List().size() + requestDTO.getTeam2List().size() + requestDTO.getNoTeamList().size();
        if (totalSummonerSize != 10) {
            throw new IllegalArgumentException("필요 인원은 10명 입니다.");
        }
        if (requestDTO.getTeam1List().size() > 5 || requestDTO.getTeam2List().size() > 5) {
            throw new IllegalArgumentException("각 팀 최대 인원은 5명입니다.");
        }
    }

}
