package carrotbat410.lol.service;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import carrotbat410.lol.utils.RiotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class TeamService {

    @Autowired
    RiotUtils riotUtils;

    public TeamAssignResponseDTO makeResultWithRandomMode(TeamAssignRequestDTO requestDTO) {
        //* Request Response 의 타입을 인터페이스인 List로 선언하니 훨 나은듯
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

        TeamAssignResponseDTO result = new TeamAssignResponseDTO(team1List, team2List, team1AvgMmrToString, team2AvgMmrToString);
        return result;
    }

    public TeamAssignResponseDTO makeResultWithBalanceMode(TeamAssignRequestDTO requestDTO) {
        return new TeamAssignResponseDTO();
    }
    public TeamAssignResponseDTO makeResultWithGoldenBalanceMode(TeamAssignRequestDTO requestDTO) {
        return new TeamAssignResponseDTO();
    }

    public static List<Integer> selectNumbers(int n, int m) {

        // 숫자 리스트 생성
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            numbers.add(i);
        }

        // 리스트 섞기
        Collections.shuffle(numbers);

        // 앞에서 m개를 선택
        return new ArrayList<>(numbers.subList(0, m));
    }

}
