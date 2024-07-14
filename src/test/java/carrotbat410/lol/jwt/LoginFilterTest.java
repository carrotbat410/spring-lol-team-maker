package carrotbat410.lol.jwt;

import carrotbat410.lol.dto.auth.JoinDTO;
import carrotbat410.lol.dto.auth.LoginDTO;
import carrotbat410.lol.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("해당하는 유저가 존재하지 않을 경우 401 예외를 던진다.")
    void loginWithNotExistingUser() throws Exception {
        // given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("asd123");
        loginDTO.setPassword("asd123");

        // when // then
        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(loginDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    @DisplayName("application/json이외의 contentType으로 요청할 경우, 400 예외를 던진다.")
    void loginWithInvalidContentType() throws Exception {
        // given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("wrongUsername");
        loginDTO.setPassword("wrongPassword");

        // when // then
        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(loginDTO))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Not Supported Content-Type"));
    }

    @Test
    @DisplayName("로그인에 성공하는 경우, 응답 헤더 Authorization key에 Bearer 토큰이 존재한다.")
    void LoginSuccess() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("test123", "asd123");
        userService.joinProcess(joinDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("test123");
        loginDTO.setPassword("asd123");

        // when
        ResultActions result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)));

        // then
        result.andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().string("Authorization", startsWith("Bearer ")));
    }

}