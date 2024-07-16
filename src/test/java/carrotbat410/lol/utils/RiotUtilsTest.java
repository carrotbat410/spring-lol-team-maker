package carrotbat410.lol.utils;

import carrotbat410.lol.dto.riot.RiotAccountApiResponseDTO;
import carrotbat410.lol.dto.riot.RiotLeagueApiResponseDTO;
import carrotbat410.lol.dto.riot.RiotSummonerApiResponseDTO;
import carrotbat410.lol.dto.riot.SummonerApiTotalDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RiotUtilsTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RiotUtils riotUtils;

//TODO RiotUtils에 apiKey, riotApiUrl null로 값 못가져오더라. => 일단 Reflection으로 해결함.
//    @Value("${riot.api.key}")
//    private String apiKey;
//
//    @Value("${riot.api.url}")
//    private String riotApiUrl;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(riotUtils, "apiKey", "test_api_key");
        ReflectionTestUtils.setField(riotUtils, "riotApiUrl", "test_riot_api_url");
    }

    @Test
    @DisplayName("Test getSummoner method - Successful case")
    void testGetSummoner_Success() {
        // Given
        String summonerName = "testSummoner";
        String tagLine = "1234";
        String puuid = "testPuuid";
        String summonerId = "testSummonerId";
        int profileIconId = 123;
        int summonerLevel = 100;
        String queueType = "RANKED_SOLO_5x5";
        String tier = "SILVER";
        String rank = "IV";
        int leaguePoints = 4;
        int wins = 50;
        int losses = 50;

        RiotAccountApiResponseDTO mockAccountResponse = new RiotAccountApiResponseDTO(puuid, summonerName, tagLine);
        RiotSummonerApiResponseDTO mockSummonerResponse = new RiotSummonerApiResponseDTO(summonerId, profileIconId, summonerLevel);
        RiotLeagueApiResponseDTO mockLeagueResponse = new RiotLeagueApiResponseDTO(queueType, tier, rank, leaguePoints, wins, losses);

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(RiotAccountApiResponseDTO.class))).thenReturn(mockAccountResponse);
        when(restTemplate.getForObject(any(URI.class), Mockito.eq(RiotSummonerApiResponseDTO.class))).thenReturn(mockSummonerResponse);
        when(restTemplate.getForObject(any(URI.class), Mockito.eq(RiotLeagueApiResponseDTO[].class))).thenReturn(new RiotLeagueApiResponseDTO[]{mockLeagueResponse});

        // When
        SummonerApiTotalDTO result = riotUtils.getSummoner(summonerName, tagLine);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSummonerName()).isEqualTo(summonerName);
        assertThat(result.getTagLine()).isEqualTo(tagLine);
        assertThat(result.getTier()).isEqualTo(tier);
        assertThat(result.getRank()).isEqualTo(4);
        assertThat(result.getMmr()).isEqualTo(9);
        assertThat(result.getLevel()).isEqualTo(summonerLevel);
        assertThat(result.getWins()).isEqualTo(wins);
        assertThat(result.getLosses()).isEqualTo(losses);
        assertThat(result.getIconId()).isEqualTo(profileIconId);
    }

}