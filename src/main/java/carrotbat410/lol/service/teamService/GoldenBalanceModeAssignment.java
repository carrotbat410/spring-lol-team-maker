package carrotbat410.lol.service.teamService;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import carrotbat410.lol.utils.RiotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
        Collections.shuffle(noTeamList); //한번 섞어서 넣기
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
        bestTeam1AvgMmr = Math.round(bestTeam1MmrSum / 5);
        bestTeam2AvgMmr = Math.round(bestTeam2MmrSum / 5);
    }

    private void DFS(int L, int s, int tmpTeam1MmrSum) {
        if(L == r) {
            int tmpTeam2MmrSum = totalMmrSum - tmpTeam1MmrSum;
            int tmpMmrDiif = Math.abs(tmpTeam1MmrSum - tmpTeam2MmrSum);

            //#1. mmr 차이 적은 결과 발견하면 결과 저장하기.
            if(tmpMmrDiif < mmrDiff) {

                //#2. newBestTeam1List 만들기
                List<SummonerDTO> newBestTeam1List = new ArrayList<>(team1List);
                HashMap<Integer, SummonerDTO> clonedNoTeamList = new HashMap<>();
                for(int i = 0; i < n; i++) clonedNoTeamList.put(i, noTeamList.get(i));

                for (Integer i : selectedTeam1IdxList) {
                    newBestTeam1List.add(clonedNoTeamList.get(i));
                    clonedNoTeamList.remove(i);
                }

                //#2. newBestTeam2List 만들기
                List<SummonerDTO> newBestTeam2List = new ArrayList<>(team2List);
                for (SummonerDTO summonerDTO : clonedNoTeamList.values()) {
                    newBestTeam2List.add(summonerDTO);
                }

                //#3. 필드에 현재값으로 저장하기
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
