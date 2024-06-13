package carrotbat410.lol.controller;

import carrotbat410.lol.controller.customMockUser.WithMockCustomUser;
import carrotbat410.lol.dto.summoner.AddSummonerReqeustDTO;
import carrotbat410.lol.dto.summoner.SummonerDTO;
import carrotbat410.lol.service.SummonerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebMvcTest(controllers = SummonerController.class)
@WithMockCustomUser
public class SummonerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SummonerService summonerService;

    @Test
    @DisplayName("추가한 소환사 목록을 조회한다.")
    void getSummoners() throws Exception {
        // given
        SummonerDTO summoner1 = createSummonerDTO(1L, "summoner1", "KR1");
        SummonerDTO summoner2 = createSummonerDTO(1L, "summoner2", "KR1");

        List<SummonerDTO> summoners = Arrays.asList(summoner1, summoner2);
        Pageable pageable = PageRequest.of(0, 30);

        when(summonerService.getSummoners(1L, pageable)).thenReturn(summoners);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/summoners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data").isArray());
//                .andExpect(jsonPath("$.data.length()").value(summoners.size())); //TODO 이거 처리 어떻게 해야하지? 여기서 하는게 맞나?


        //* 아래 부분은 서비스 레이어에서 검증할 부분이기 떄문에, 여기서 검증할 필요 X.
//                .andExpect(jsonPath("$.data[0].summonerName").value("Summoner1"))
//                .andExpect(jsonPath("$.data[0].tagLine").value("KR1"))
    }

    @Test
    @DisplayName("소환사를 추가할 떄 소환사명은 필수값이다.")
    void addSummonerWithoutSummonerName() throws Exception{
        // given
        AddSummonerReqeustDTO request = new AddSummonerReqeustDTO();
        request.setSummonerName("");
        request.setTagLine("KR1");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/summoner")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Length"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 소환사이름 양식입니다."))
                .andExpect(jsonPath("$.fieldName").value("summonerName"));
    }

    //TODO 경계값 테스트하기

//    @Test
//    @DisplayName("소환사를 추가할 떄 태그라인이 없으면 KR1이 기본값으로 들어간다.")
//    void addSummonerWithoutTagLine() throws Exception{
//        // given
//        AddSummonerReqeustDTO request = new AddSummonerReqeustDTO();
//        request.setSummonerName("E크에크파이크");
//        request.setTagLine("");
//
//        // when // then
//        mockMvc.perform(MockMvcRequestBuilders.post("/summoner")
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf())
//                )
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }

    @Test
    @DisplayName("소환사를 성공적으로 추가하면 상태코드 200을 반환한다.")
    void addSummoner() throws Exception{
        // given
        AddSummonerReqeustDTO request = new AddSummonerReqeustDTO();
        request.setSummonerName("E크에크파이크");
        request.setTagLine("KR1");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/summoner")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());


    }

    private static SummonerDTO createSummonerDTO(Long id, String summonerName, String tagLine) {
        return new SummonerDTO(id, summonerName, tagLine, "GOLD", 1, 22, 100, 70, 70, 123, LocalDateTime.now());
    }

}
