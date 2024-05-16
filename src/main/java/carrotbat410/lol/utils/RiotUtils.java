package carrotbat410.lol.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
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
        //TODO 추가적인 예외처리

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

        return restTemplate.getForObject(uri, AccountInfoDTO.class);
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

        return restTemplate.getForObject(uri, SummonerInfoDTO.class);
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




    @Getter
    @ToString
    public static class AccountInfoDTO {
        private String puuid;
        private String gameName;
        private String tagLine;
    }

    @Getter
    @ToString
    public static class SummonerInfoDTO {
        private String id;
        private int profileIconId;
        private int summonerLevel;
    }

    /**
     * leagueInfoRequest = [
     * {
     * "queueType":"RANKED_SOLO_5x5" ||
     * "tier":"MASTER",
     * "rank":"I",
     * "summonerId":"Xh870oQDH4F_Cm6bUF88JFonjzLANXcgLf6kKLPx7oToYmw",
     * "leaguePoints":227,
     * "wins":28,
     * "losses":11,
     * }
     * ]
     */
    @Getter
    @ToString
    public static class LeagueInfoDTO {
        private String queueType;
        private String tier;
        private String rank;
        private String summonerId;
        private int leaguePoints;
        private int wins;
        private int losses;
    }

}
