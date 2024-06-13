package carrotbat410.lol.controller;

import carrotbat410.lol.controller.customMockUser.WithMockCustomUser;
import carrotbat410.lol.dto.summoner.AddSummonerReqeustDTO;
import carrotbat410.lol.service.SummonerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
    @DisplayName("소환사를 추가할 떄 소환사명은 필수이다.")
    void addSummonerWithoutSummonerName() throws Exception{
        // given
        AddSummonerReqeustDTO request = new AddSummonerReqeustDTO();
        request.setSummonerName("");
        request.setTagLine("KR1");

        String body = objectMapper.writeValueAsString(request);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/summoner")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    @DisplayName("소환사를 성공적으로 추가하면 상태코드 200을 반환한다.")
    void addSummoner() throws Exception{
        // given
        AddSummonerReqeustDTO request = new AddSummonerReqeustDTO();
        request.setSummonerName("E크에크파이크");
        request.setTagLine("KR1");

        String body = objectMapper.writeValueAsString(request);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/summoner")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());


    }

}
