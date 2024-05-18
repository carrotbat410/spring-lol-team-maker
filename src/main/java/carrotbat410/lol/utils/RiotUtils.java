package carrotbat410.lol.utils;

import carrotbat410.lol.dto.SummonerDTO;
import carrotbat410.lol.dto.riot.AccountInfoDTO;
import carrotbat410.lol.dto.riot.LeagueInfoDTO;
import carrotbat410.lol.dto.riot.SummonerInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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

    public SummonerDTO getSummoner(String summonerName, String tagLine) {

        //TODO 추가적인 예외처리 (e.g getPuuid() 없을 경우, 요청 제한 초과 에러, API 서버 안될떄)
        //TODO 에러 메세지 텔레그램 전송 추가하기

        //TODO Limit Rate 20 requests every 1 seconds, 100 requests every 2 minutes 인데, 1초에 20번 요청만 확인하기.
        //TODO League API 30 requests every 10 seconds 는 뭐지?
        AccountInfoDTO accountInfo = getAccountInfo(summonerName, tagLine);
        SummonerInfoDTO summonerInfo = getSummonerInfo(accountInfo.getPuuid());
        LeagueInfoDTO leagueInfo = getLeagueInfo(summonerInfo.getId());
        //TODO 추가적인 예외처리
        //TODO 에러 메세지 텔레그램 전송 추가하기

        return SummonerDTO.of(accountInfo.getGameName(),
                accountInfo.getTagLine(), leagueInfo.getTier(), leagueInfo.getRank(), summonerInfo.getSummonerLevel(),
                leagueInfo.getWins(), leagueInfo.getLosses(), summonerInfo.getProfileIconId());
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
            if(leagueInfoDTO.getQueueType().equals("RANKED_SOLO_5x5")) {
                leagueInfo = leagueInfoDTO;
            }
        }

        if(leagueInfo == null) {
            leagueInfo = new LeagueInfoDTO("RANKED_SOLO_5x5", "UNRANKED", null, 0, 0, 0);
        }

        return leagueInfo;
    }

}
