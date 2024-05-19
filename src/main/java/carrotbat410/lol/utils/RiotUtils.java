package carrotbat410.lol.utils;

import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.dto.riot.AccountApiResponseDTO;
import carrotbat410.lol.dto.riot.LeagueApiResponseDTO;
import carrotbat410.lol.dto.riot.SummonerApiResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

//TODO @Component vs static 유틸 클래스는 어떤게 더 나은지 찾아보기
@Component
public class RiotUtils {
    @Value("${riot.api.key}")
    private String apiKey;

    @Value("${riot.api.url}")
    private String riotApiUrl;

    private final Map<String, Integer> TIER_BASE_POINTS = new HashMap<>();

     {
        TIER_BASE_POINTS.put("IRON", 0);
        TIER_BASE_POINTS.put("BRONZE", 4);
        TIER_BASE_POINTS.put("SILVER", 8);
        TIER_BASE_POINTS.put("GOLD", 12);
        TIER_BASE_POINTS.put("PLATINUM", 16);
        TIER_BASE_POINTS.put("EMERALD", 20);
        TIER_BASE_POINTS.put("DIAMOND", 24);
    }

    //! 부하테스트할떄 진짜 api 요청하면 제한먹으니 조심.
    //TODO WebClient로 바꾸기???
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    public SummonerDTO getSummoner(String summonerName, String tagLine) {

        //TODO 추가적인 예외처리 (e.g getPuuid() 없을 경우, 요청 제한 초과 에러, API 서버 안될떄)
        //TODO 에러 메세지 텔레그램 전송 추가하기

        //TODO Limit Rate 20 requests every 1 seconds, 100 requests every 2 minutes 인데, 1초에 20번 요청만 확인하기.
        //TODO League API 30 requests every 10 seconds 는 뭐지?
        AccountApiResponseDTO accountInfo = getAccountInfo(summonerName, tagLine);
        SummonerApiResponseDTO summonerInfo = getSummonerInfo(accountInfo.getPuuid());
        LeagueApiResponseDTO leagueInfo = getLeagueInfo(summonerInfo.getId());

//        System.out.println("accountInfo = " + accountInfo);
//        System.out.println("summonerInfo = " + summonerInfo);
//        System.out.println("leagueInfo = " + leagueInfo);

        Integer numberRank = RankStringToNumber(leagueInfo.getRank());

        Integer mmr = CalculatMmr(leagueInfo.getTier(), numberRank);

        return SummonerDTO.of(accountInfo.getGameName(),
                accountInfo.getTagLine(), leagueInfo.getTier(), numberRank, mmr, summonerInfo.getSummonerLevel(),
                leagueInfo.getWins(), leagueInfo.getLosses(), summonerInfo.getProfileIconId());
    }

    private AccountApiResponseDTO getAccountInfo(String summonerName, String tagLine) {

        URI uri = UriComponentsBuilder
                .fromUriString("https://asia.api.riotgames.com")
                .path("/riot/account/v1/accounts/by-riot-id/{summonerName}/{tagLine}")
                .queryParam("api_key", apiKey)
                .encode()
                .build()
                .expand(summonerName, tagLine)
                .toUri();

        return restTemplate.getForObject(uri, AccountApiResponseDTO.class);
    }

    private SummonerApiResponseDTO getSummonerInfo(String puuid) {
        URI uri = UriComponentsBuilder
                .fromUriString(riotApiUrl)
                .path("/lol/summoner/v4/summoners/by-puuid/{puuid}")
                .queryParam("api_key", apiKey)
                .encode()
                .build()
                .expand(puuid)
                .toUri();

        return restTemplate.getForObject(uri, SummonerApiResponseDTO.class);
    }

    private LeagueApiResponseDTO getLeagueInfo(String summonerId) {
        URI uri = UriComponentsBuilder
                .fromUriString(riotApiUrl)
                .path("/lol/league/v4/entries/by-summoner/{summonerId}")
                .queryParam("api_key", apiKey)
                .encode()
                .build()
                .expand(summonerId)
                .toUri();


        LeagueApiResponseDTO[] leagueInfoArr = restTemplate.getForObject(uri, LeagueApiResponseDTO[].class);

        LeagueApiResponseDTO leagueInfo = null;
        for (LeagueApiResponseDTO leagueApiResponseDTO : leagueInfoArr) {
            if(leagueApiResponseDTO.getQueueType().equals("RANKED_SOLO_5x5")) {
                leagueInfo = leagueApiResponseDTO;
            }
        }

        if(leagueInfo == null) return new LeagueApiResponseDTO("RANKED_SOLO_5x5", "UNRANKED", null, 0, 0, 0);

        return leagueInfo;
    }

    private Integer RankStringToNumber(String rank) {
        if(StringUtils.isNotBlank(rank)) {
            if (rank.equals("I")) return 1;
            if(rank.equals("II")) return 2;
            if(rank.equals("III")) return 3;
            if(rank.equals("IV")) return 4;
        }
        return null;
    }

    private Integer CalculatMmr(String tier, Integer rank) {
        if(tier.equals("UNRANKED")) return 0;
        if(tier.equals("MASTER")) return 29;
        if(tier.equals("GRANDMASTER")) return 30;
        if (tier.equals("CHALLENGER")) return 31;

        /**
         * 마스터,그마,챌린저 점수 세분화 추가할 경우 참고사항
         * 마스터, 그마, 챌린저는 모두 rank=I로 제공함. leaguePoints=557를 참고해야함.
         */

        // IRON 4(1) ~ DIAMOND 1(28) 사이인 경우
        int basePoints = TIER_BASE_POINTS.getOrDefault(tier, 0);
        int rankPoints = 5 - rank;
        return basePoints + rankPoints;
    }

}
