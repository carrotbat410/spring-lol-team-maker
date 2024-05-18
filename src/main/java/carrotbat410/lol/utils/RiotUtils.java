package carrotbat410.lol.utils;

import carrotbat410.lol.entity.Summoner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
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

    //! 부하테스트할떄 진짜 api 요청하면 제한먹으니 조심.
    //TODO WebClient로 바꾸기???
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    public void getSummoner(String summonerName, String tagLine) {
        AccountInfoDTO accountInfo = getAccountInfo(summonerName, tagLine);
        SummonerInfoDTO summonerInfo = getSummonerInfo(accountInfo.getPuuid());
        LeagueInfoDTO leagueInfo = getLeagueInfo(summonerInfo.getId());
        //TODO 추가적인 예외처리
        //TODO 에러 메세지 텔레그램 전송 추가하기

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

    private LeagueInfoDTO getLeagueInfo(String summonerId) {
        URI uri = UriComponentsBuilder
                .fromUriString(riotApiUrl)
                .path("/lol/league/v4/entries/by-summoner/{summonerId}")
                .queryParam("api_key", apiKey)
                .encode()
                .build()
                .expand(summonerId)
                .toUri();


        LeagueInfoDTO[] leagueInfoArr = restTemplate.getForObject(uri, LeagueInfoDTO[].class);

        LeagueInfoDTO leagueInfo = null;
        for (LeagueInfoDTO leagueInfoDTO : leagueInfoArr) {
            if(leagueInfoDTO.getQueueType() == "RANKED_SOLO_5x5") {
                leagueInfo = leagueInfoDTO;
            }
        }

        if(leagueInfo == null) {
            leagueInfo = new LeagueInfoDTO("RANKED_SOLO_5x5", "UNRANKED", null, 0, 0, 0);
        }

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
    @AllArgsConstructor
    public static class LeagueInfoDTO {
        private String queueType;
        private String tier;
        private String rank;
        private int leaguePoints;
        private int wins;
        private int losses;
    }

}
