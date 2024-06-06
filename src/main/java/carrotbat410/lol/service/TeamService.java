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
        ArrayList<SummonerDTO> team1List = (ArrayList<SummonerDTO>) requestDTO.getTeam1List();
        ArrayList<SummonerDTO> team2List = (ArrayList<SummonerDTO>) requestDTO.getTeam2List();
        ArrayList<SummonerDTO> noTeamList = (ArrayList<SummonerDTO>) requestDTO.getNoTeamList();

        GoldenBalanceUtils result = new GoldenBalanceUtils(team1List, team2List, noTeamList);
        return new TeamAssignResponseDTO(result.bestTeam1List, result.bestTeam2List, riotUtils.mmrToString(result.bestTeam1AvgMmr), riotUtils.mmrToString(result.bestTeam2AvgMmr));
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

class GoldenBalanceUtils {
    private List<SummonerDTO> team1List;
    private List<SummonerDTO> team2List;
    private List<SummonerDTO> noTeamList;
    public List<SummonerDTO> bestTeam1List;
    public List<SummonerDTO> bestTeam2List;
    public int bestTeam1MmrSum, bestTeam2MmrSum;
    public int bestTeam1AvgMmr, bestTeam2AvgMmr;
    public int totalMmrSum;
    public int mmrDiff = Integer.MAX_VALUE;
    private int n; //n개 중에
    private int r; //r개 뽑는 조합
    private Integer[] selectedTeam1IdxList;

    public GoldenBalanceUtils(
            List<SummonerDTO> team1List, List<SummonerDTO> team2List, List<SummonerDTO> noTeamList
    ) {
        //#1. 모든값 초기화
        this.team1List = team1List;
        this.team2List = team2List;
        this.noTeamList = noTeamList;
        this.bestTeam1List = new ArrayList<>();
        this.bestTeam2List = new ArrayList<>();
        for (int i = 0; i < team1List.size(); i++) {
            bestTeam1List.add(team1List.get(i));
            bestTeam1MmrSum += team1List.get(i).getMmr();
            totalMmrSum += team1List.get(i).getMmr();
        }
        for (int i = 0; i < team2List.size(); i++) {
            bestTeam2List.add(team2List.get(i));
            bestTeam2MmrSum += team2List.get(i).getMmr();
            totalMmrSum += team2List.get(i).getMmr();
        }
        for (int i = 0; i < noTeamList.size(); i++) totalMmrSum += noTeamList.get(i).getMmr();
        n = noTeamList.size();
        r = 5 - team1List.size();
        selectedTeam1IdxList = new Integer[r];

        int initTeam1MmrSum = 0;
        for (SummonerDTO team1Member : team1List) initTeam1MmrSum += team1Member.getMmr();
        DFS(0, 0, initTeam1MmrSum);
    }

    public void DFS(int L, int s, int tmpTeam1MmrSum) {
        if(L == r) {
            int tmpTeam2MmrSum = totalMmrSum - tmpTeam1MmrSum;
            int tmpMmrDiif = Math.abs(tmpTeam1MmrSum - tmpTeam2MmrSum);

            //#1. mmr 차이 적은 결과 발견
            if(tmpMmrDiif < mmrDiff) {
                //#2. newBestTeamList 만들기
                List<SummonerDTO> clonedNoTeamList = new ArrayList<>(noTeamList);
                List<SummonerDTO> newBestTeam1List = new ArrayList<>(team1List);
                for (Integer i : selectedTeam1IdxList) {
                    newBestTeam1List.add(clonedNoTeamList.get(i));
                    clonedNoTeamList.remove(i);
                }
                List<SummonerDTO> newBestTeam2List = new ArrayList<>(team2List);
                newBestTeam2List.addAll(noTeamList);

                //#3. 필드 값 바꾸기
                mmrDiff = tmpMmrDiif;
                bestTeam1MmrSum = tmpTeam1MmrSum;
                bestTeam2MmrSum = tmpTeam2MmrSum;
                bestTeam1List = newBestTeam1List;
                bestTeam2List = newBestTeam2List;
            }
        }else {
            for(int i = s; i < n; i++) {
                SummonerDTO target = noTeamList.get(i);
                selectedTeam1IdxList[L] = i;
                DFS(L + 1, i + 1, tmpTeam1MmrSum + target.getMmr());
            }
        }

    }

}
