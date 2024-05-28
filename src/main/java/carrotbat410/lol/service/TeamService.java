package carrotbat410.lol.service;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class TeamService {

    //TODO (모니터링, 부하 테스트 먼저 만든 후)Random, balance, goldenBalance 개선하기.
    public TeamAssignResponseDTO makeResultWithRandomMode(TeamAssignRequestDTO requestDTO) {
        ArrayList<SummonerDTO> finalTeam1List = new ArrayList<>();
        ArrayList<SummonerDTO> finalTeam2List = new ArrayList<>();
        int team1AvgMmrSum = 0;
        int team2AvgMmrSum = 0;
        HashMap<Integer, SummonerDTO> tmpNoTeamList = new HashMap<>();

        for (SummonerDTO summonerDTO : finalTeam1List) finalTeam1List.add(summonerDTO);
        for (SummonerDTO summonerDTO : finalTeam2List) finalTeam2List.add(summonerDTO);
        for(int i = 0; i < requestDTO.getNoTeamList().length; i++) tmpNoTeamList.put(i, requestDTO.getNoTeamList()[i]);

        int requiredTeam1Cnt = 5 - requestDTO.getTeam1List().length;
        List<Integer> selectedNumbers = selectNumbers(requestDTO.getNoTeamList().length, requiredTeam1Cnt);

        System.out.println("selectedNumbers = " + selectedNumbers);
        for (int selectedNumber : selectedNumbers) {
            team1AvgMmrSum += tmpNoTeamList.get(selectedNumber).getMmr();
            finalTeam1List.add(tmpNoTeamList.get(selectedNumber));
            tmpNoTeamList.remove(selectedNumber);
        }
        System.out.println("남아있는 인원:" + tmpNoTeamList.size());
        for (Integer key : tmpNoTeamList.keySet()) {
            team2AvgMmrSum += tmpNoTeamList.get(key).getMmr();
            finalTeam2List.add(tmpNoTeamList.get(key));
        }

        Integer team1AvgMmr = Math.round(team1AvgMmrSum / 5);
        Integer team2AvgMmr = Math.round(team2AvgMmrSum / 5);


        TeamAssignResponseDTO result = new TeamAssignResponseDTO(finalTeam1List, finalTeam2List, "임시!! GOLD 2", "임시!! GOLD 2");
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
