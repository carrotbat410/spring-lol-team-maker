package carrotbat410.lol.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

//TODO @Component vs static 유틸 클래스는 어떤게 더 나은지 찾아보기
@Component
public class RiotUtils {
    @Value("${riot.api.key}")
    private String apiKey;

    @Value("${riot.api.url}")
    public String riotApiUrl;

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    public void getSummoner(String summonerName, String tagLine) {

        AccountInfoDTO accountInfo = getAccountInfo(summonerName, tagLine);
        SummonerInfoDTO summonerInfo = getSummonerInfo(accountInfo.getPuuid());
        List<LeagueInfoDTO> leagueInfo = getLeagueInfo(summonerInfo.getId());
        //TODO getLeagueInfo 맵핑 마무리하기
        //TODO 각 메서드 중복제거 및 최적화하기.
        //TODO 맵핑할떄 DTO랑 형식 정확히 일치해야하는데 이 문제 해결할 방법 없나?(없으면 그냥 null로 맵핑되도록)
        //TODO 예외처리 제대로 걸리는지 확인 및 추가 예외처리

    }

    private AccountInfoDTO getAccountInfo(String summonerName, String tagLine) {

        URI uri = UriComponentsBuilder
                .fromUriString("https://asia.api.riotgames.com")
                .path("/riot/account/v1/accounts/by-riot-id/{summonerName}/{tagLine}")
                .queryParam("api_key", apiKey)
                .encode()
                .build()
                .expand(summonerName, tagLine)
                .toUri();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        String accountInfoRequest = responseEntity.getBody();

        AccountInfoDTO accountInfoDTO = null; //TODO accountInfo가 더 나은거 같은데

        try {
            accountInfoDTO = mapper.readValue(accountInfoRequest, AccountInfoDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); //TODO RuntimeException말고 다른 에러 반환해서 Advice에서 받기.
        }
        return accountInfoDTO;
    }

    private SummonerInfoDTO getSummonerInfo(String puuid) {
        URI uri = UriComponentsBuilder
                .fromUriString(riotApiUrl)
                .path("/lol/summoner/v4/summoners/by-puuid/{puuid}")
                .queryParam("api_key", apiKey)
                .encode()
                .build()
                .expand(puuid)
                .toUri();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        String summonerInfoRequest = responseEntity.getBody();

        SummonerInfoDTO summonerInfo = null;
        try {
            summonerInfo = mapper.readValue(summonerInfoRequest, SummonerInfoDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); //TODO RuntimeException말고 다른 에러 반환해서 Advice에서 받기.
        }
        return summonerInfo;
    }

    private List<LeagueInfoDTO> getLeagueInfo(String summonerId) {
        URI uri = UriComponentsBuilder
                .fromUriString(riotApiUrl)
                .path("/lol/league/v4/entries/by-summoner/{summonerId}")
                .queryParam("api_key", apiKey)
                .encode()
                .build()
                .expand(summonerId)
                .toUri();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        String leagueInfoRequest = responseEntity.getBody();
        System.out.println("----------------------------------------------------------------");
        System.out.println("leagueInfoRequest = " + leagueInfoRequest);

        List<LeagueInfoDTO> leagueInfo = new ArrayList<>();
        System.out.println("leagueInfoRequest.length() = " + leagueInfoRequest.length());

        if(leagueInfoRequest.equals("[]")) return leagueInfo; //TODO 이게 최선인가?
//        if(leagueInfoRequest.)
        
//        try {
//            leagueInfo = mapper.readValue(leagueInfoRequest, LeagueInfoDTO.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        System.out.println("최종 리턴값"+leagueInfo);
        return leagueInfo;
    }




    @Data
    public static class AccountInfoDTO {
        private String puuid;
        private String gameName;
        private String tagLine;
    }

    @Data
    public static class SummonerInfoDTO {
        private String id;
        private String accountId;
        private String puuid;
        private long revisionDate;
        private int profileIconId;
        private int summonerLevel;
    }

    /**
     * leagueInfoRequest = [
     * {
     * "leagueId":"64de9d2d-89e0-3698-a7cd-a9b7e04ef3b6",
     * "queueType":"RANKED_SOLO_5x5",
     * "tier":"MASTER",
     * "rank":"I",
     * "summonerId":"Xh870oQDH4F_Cm6bUF88JFonjzLANXcgLf6kKLPx7oToYmw",
     * "leaguePoints":227,
     * "wins":28,
     * "losses":11,
     * "veteran":false,
     * "inactive":false,
     * "freshBlood":true,
     * "hotStreak":true
     * }
     * ]
     */
    @Data
    public static class LeagueInfoDTO {
        private String leagueId;
        private String queueType;
        private String tier;
        private String rank;
        private String summonerId;
        private int leaguePoints;
        private int wins;
        private int losses;
        private boolean veteran;
        private boolean inactive;
        private boolean freshBlood;
        private boolean hotStreak;
    }

}
