package carrotbat410.lol.controller;

import carrotbat410.lol.dto.auth.JoinDTO;
import carrotbat410.lol.service.UserService;
import carrotbat410.lol.utils.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@WithMockUser
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    //TODO 로그인 테스트 코드 작성하기

    @Test
    @DisplayName("username이 null이면 예외를 던진다.")
    void joinWithEmptyUsername() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO(null, "asd123");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("username"))
                .andExpect(jsonPath("$.message").value("아이디를 입력해주세요."));
    }

    @Test
    @DisplayName("username은 특수 문자를 포함할 수 없다.")
    void joinWithUsernameIncludedSpecialLetter() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("asd!@#", "asd123");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("username"))
                .andExpect(jsonPath("$.message").value("아이디에 특수 문자는 사용할 수 없습니다."));
    }

    @Test
    @DisplayName("username의 길이가 6글자 미만이면 예외를 던진다.")
    void joinWithShortUsername() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("test1", "asd123");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("username"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 아이디 양식입니다."));
    }

    @Test
    @DisplayName("username의 길이가 20글자 초과하면 예외를 던진다.")
    void joinWithLongUsername() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("123456789012345678901", "asd123");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("username"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 아이디 양식입니다."));
    }

    @Test
    @DisplayName("password가 null이면 예외를 던진다.")
    void joinWithEmptyPassword() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("test123", null);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("password"))
                .andExpect(jsonPath("$.message").value("패스워드를 입력해주세요"));
    }

    @Test
    @DisplayName("password의 길이가 6글자 미만이면 예외를 던진다.")
    void joinWithShortPassword() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("test123", "asd12");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("password"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 패스워드 양식입니다."));
    }

    @Test
    @DisplayName("password의 길이가 20글자 초과이면 예외를 던진다.")
    void joinWithLongPassword() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("test123", "123456789012345678901");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldName").value("password"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 패스워드 양식입니다."));
    }


    @Test
    @DisplayName("유효성 검사를 통과하면 회원가입에 성공한다.")
    //TODO @WithAnonymousUser가 더 알맞는거 아닐까?
    void joinSuccess() throws Exception {
        // given
        JoinDTO joinDTO = new JoinDTO("test123", "asd123");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/join")
                        .content(objectMapper.writeValueAsString(joinDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    @DisplayName("test1계정은 샘플 계정이므로, 회원 탈퇴할 수 없다.")
    @WithMockUser //! 로그인한 상태이여야함,
    void deleteSampleUser() throws Exception {
        // given
        //* 정적 메서드(=SecurityUtils내부 메서드)를 mocking하려면, mockito-inline를 사용하여 런타임시 Bytecode를 조작해야한다.
        // when(SecurityUtils.getCurrentUsernameFromAuthentication()).thenReturn("test1");

        //* mockito-inline 사용한 mocking
        //! mockedStatic.close(); //! 자원에 대한 close() 를 호출하거나 try-with-resources 사용하여 MockedStatic리소스를 자동으로 닫아주어야 한다.
        //! close 하지 않으면 해당 Thread는 활성 Thread로 남기 때문에 다른 테스트에 영향을 줄 수 있다.
        try(MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            given(SecurityUtils.getCurrentUsernameFromAuthentication()).willReturn("test1");


            // when // then
            mockMvc.perform(MockMvcRequestBuilders.delete("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf())
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("test 계정은 회원 탈퇴할 수 없습니다."));
        }

    }

    @Test
    @DisplayName("유저는 회원 탈퇴를 할 수 있다.")
    @WithMockUser
    void deleteUserSuccess() throws Exception {
        // given
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)){
            given(SecurityUtils.getCurrentUsernameFromAuthentication()).willReturn("test123");
            given(SecurityUtils.getCurrentUserIdFromAuthentication()).willReturn(1L);

            // when // then
            mockMvc.perform(MockMvcRequestBuilders.delete("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf())
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("ok"));
        }

    }

}