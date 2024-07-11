package carrotbat410.lol.service.teamService;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import carrotbat410.lol.utils.RiotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RandomModeAssignment implements TeamAssignmentStrategy {

    private final RiotUtils riotUtils;

    @Autowired
    public RandomModeAssignment(RiotUtils riotUtils) {
        this.riotUtils = riotUtils;
    }

    @Override
    public TeamAssignResponseDTO assignTeams(TeamAssignRequestDTO requestDTO) {
        ArrayList<SummonerDTO> team1List = (ArrayList<SummonerDTO>) requestDTO.getTeam1List();
        ArrayList<SummonerDTO> team2List = (ArrayList<SummonerDTO>) requestDTO.getTeam2List();
        ArrayList<SummonerDTO> noTeamList = (ArrayList<SummonerDTO>) requestDTO.getNoTeamList();

        //#1. 인덱스 0 ~ noTeamList.size() 사이 숫자에서 requiredTeam1Cnt개 뽑기.
        int requiredTeam1Cnt = 5 - requestDTO.getTeam1List().size();
        List<Integer> selectedNumbers = selectNumbers(noTeamList.size(), requiredTeam1Cnt);

        //#2. noTeamList -> team1으로 옮기기
        for (Integer selectedNumber : selectedNumbers) {
            team1List.add(noTeamList.get(selectedNumber));
            noTeamList.remove(selectedNumber);
        }

        //#3. noTeamList에 남아있는 나머지 전부 -> team2로 옮기기
        for (SummonerDTO summonerDTO : noTeamList) team2List.add(summonerDTO);

        //#4. 각 팀의 평균 mmr 구해서 String으로 반환하기
        int team1MmrSum = 0;
        int team2MmrSum = 0;
        for (SummonerDTO summonerDTO : team1List) team1MmrSum += summonerDTO.getMmr();
        for (SummonerDTO summonerDTO : team2List) team2MmrSum += summonerDTO.getMmr();
        Integer team1AvgMmr = Math.round(team1MmrSum / 5);
        Integer team2AvgMmr = Math.round(team2MmrSum / 5);
        String team1AvgMmrToString = riotUtils.mmrToString(team1AvgMmr);
        String team2AvgMmrToString = riotUtils.mmrToString(team2AvgMmr);

        return new TeamAssignResponseDTO(team1List, team2List, team1AvgMmrToString, team2AvgMmrToString);
    }

    private List<Integer> selectNumbers(int n, int m) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        return new ArrayList<>(numbers.subList(0, m));
    }
}
