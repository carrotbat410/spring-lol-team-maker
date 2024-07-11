package carrotbat410.lol.service.teamService;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.team.TeamAssignRequestDTO;
import carrotbat410.lol.dto.team.TeamAssignResponseDTO;
import carrotbat410.lol.utils.RiotUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BalanceModeAssignment implements TeamAssignmentStrategy {

    private final RiotUtils riotUtils;

    @Autowired
    public BalanceModeAssignment(RiotUtils riotUtils) {
        this.riotUtils = riotUtils;
    }

    @Override
    public TeamAssignResponseDTO assignTeams(TeamAssignRequestDTO requestDTO) {
        ArrayList<SummonerDTO> team1List = (ArrayList<SummonerDTO>) requestDTO.getTeam1List();
        ArrayList<SummonerDTO> team2List = (ArrayList<SummonerDTO>) requestDTO.getTeam2List();
        ArrayList<SummonerDTO> noTeamList = (ArrayList<SummonerDTO>) requestDTO.getNoTeamList();

        BalanceUtils balanceUtils = new BalanceUtils(team1List, team2List, noTeamList);
        BestTeamResult result = balanceUtils.getResultOne();
        return new TeamAssignResponseDTO(result.bestTeam1List, result.bestTeam2List, riotUtils.mmrToString(result.bestTeam1AvgMmr), riotUtils.mmrToString(result.bestTeam2AvgMmr));
    }
}

class BalanceUtils {
    private List<SummonerDTO> team1List;
    private List<SummonerDTO> team2List;
    private List<SummonerDTO> noTeamList;
    public int totalMmrSum;
    private int n;
    private int r;
    private int maxResultCnt = 8; //! 결과 시드 갯수(너무 늘리면, 밸붕결과도 포함할 수 있음)
    private Integer[] selectedTeam1IdxList;
    private List<BestTeamResult> results = new ArrayList<>();

    public BalanceUtils(
            List<SummonerDTO> team1List, List<SummonerDTO> team2List, List<SummonerDTO> noTeamList
    ) {
        //#1. 모든값 초기화
        this.team1List = team1List;
        this.team2List = team2List;
        Collections.shuffle(noTeamList); //한번 섞어서 넣기
        this.noTeamList = noTeamList;

        for (int i = 0; i < team1List.size(); i++) totalMmrSum += team1List.get(i).getMmr();
        for (int i = 0; i < team2List.size(); i++) totalMmrSum += team2List.get(i).getMmr();
        for (int i = 0; i < noTeamList.size(); i++) totalMmrSum += noTeamList.get(i).getMmr();

        n = noTeamList.size();
        r = 5 - team1List.size();
        selectedTeam1IdxList = new Integer[r];

        int initTeam1MmrSum = 0;
        for (SummonerDTO team1Member : team1List) initTeam1MmrSum += team1Member.getMmr();
        DFS(0, 0, initTeam1MmrSum);
        results.sort(Comparator.comparingInt(result -> result.mmrDiff));
    }

    public BestTeamResult getResultOne() {
        int len = results.size();
        if(len < maxResultCnt) maxResultCnt = len; //결과가 적을경우, 결과 시드 갯수를 결과 사이즈만큼으로 제한하기.

        int randomIdx = (int) (Math.random() * (maxResultCnt));
        return results.get(randomIdx);
    }

    private void DFS(int L, int s, int tmpTeam1MmrSum) {
        if(L == r) {
            int tmpTeam2MmrSum = totalMmrSum - tmpTeam1MmrSum;
            int tmpMmrDiif = Math.abs(tmpTeam1MmrSum - tmpTeam2MmrSum);

            //#1. newBestTeam1List 만들기
            List<SummonerDTO> newBestTeam1List = new ArrayList<>(team1List);
            HashMap<Integer, SummonerDTO> clonedNoTeamList = new HashMap<>(); //! index가 아닌, key값 가진 컬렉션으로 해야 코드 깔끔해짐(관련: 삭제할떄 size가 줄어, 삭제할 인덱스가 없어졌던 문제)
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

            //#3. BestTeamResult[]에 결과 저장하기
            BestTeamResult newResult = new BestTeamResult(newBestTeam1List, newBestTeam2List, Math.round(tmpTeam1MmrSum / 5), Math.round(tmpTeam2MmrSum / 5), tmpMmrDiif);
            results.add(newResult);

        }else {
            for(int i = s; i < n; i++) {
                SummonerDTO target = noTeamList.get(i);
                selectedTeam1IdxList[L] = i;
                DFS(L + 1, i + 1, tmpTeam1MmrSum + target.getMmr());
            }
        }

    }
}

@AllArgsConstructor
class BestTeamResult{
    public List<SummonerDTO> bestTeam1List;
    public List<SummonerDTO> bestTeam2List;
    public int bestTeam1AvgMmr;
    public int bestTeam2AvgMmr;
    public int mmrDiff;
}