package carrotbat410.lol.jwt;

import carrotbat410.lol.dto.auth.CustomUserDetails;
import carrotbat410.lol.dto.auth.UserTokenDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class JWTFilterTest {
    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private JWTFilter jwtFilter;

    @Test
    @DisplayName("유효한 JWT인 경우, SecurityContextHolder에 Authentication을 저장한다.")
    void doFilterInternalMethodWithValidJWT() throws Exception {
        // Given
        String token = "valid_jwt_token";
        String username = "testuser";
        String role = "ROLE_USER";
        Long userId = 1L;

        // Mock JWTUtil
        when(jwtUtil.isExpired(any())).thenReturn(false);
        when(jwtUtil.getUsername(any())).thenReturn(username);
        when(jwtUtil.getRole(any())).thenReturn(role);
        when(jwtUtil.getId(any())).thenReturn(userId);

        // MockHttpServletRequest with Authorization header containing JWT
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();

        MockFilterChain filterChain = new MockFilterChain();

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        // SecurityContextHolder안에 Authentication 저장되었는지 확인.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getId()).isEqualTo(userId);
        assertThat(authentication.getAuthorities()).extracting("authority").contains(role);
    }

    @Test
    @DisplayName("만료된 JWT인 경우, ExpiredJwtException 예외를 반환한다.")
    void doFilterInternalMethodWithExpiredJWT() throws IOException, ServletException {
        // Given
        String expiredToken = "expired_jwt_token";

        // Mock JWTUtil
        JWTUtil jwtUtil = Mockito.mock(JWTUtil.class);
        when(jwtUtil.isExpired(expiredToken)).thenThrow(ExpiredJwtException.class);

        // MockHttpServletRequest with Authorization header containing expired JWT
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + expiredToken);

        MockHttpServletResponse response = new MockHttpServletResponse();

        MockFilterChain filterChain = new MockFilterChain();

        // Create JWTFilter instance
        JWTFilter jwtFilter = new JWTFilter(jwtUtil);

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).isEqualTo("Token expired");
    }

    @Test
    @DisplayName("서명이 잘못된(=조작된) JWT인 경우, SignatureException 예외를 반환한다.")
    void doFilterInternalMethodWithInvalidJWT() throws IOException, ServletException {
        // Given
        String invalidToken = "invalid_jwt_token";

        // Mock JWTUtil
        JWTUtil jwtUtil = Mockito.mock(JWTUtil.class);
        when(jwtUtil.isExpired(invalidToken)).thenThrow(SignatureException.class);

        // MockHttpServletRequest with Authorization header containing invalid JWT
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + invalidToken);

        MockHttpServletResponse response = new MockHttpServletResponse();

        MockFilterChain filterChain = new MockFilterChain();

        // Create JWTFilter instance
        JWTFilter jwtFilter = new JWTFilter(jwtUtil);

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).isEqualTo("token is invalid");
    }
}