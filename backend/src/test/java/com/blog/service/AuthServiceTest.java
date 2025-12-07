package com.blog.service;

import com.blog.config.JwtUtils;
import com.blog.dto.AuthRequest;
import com.blog.dto.AuthResponse;
import com.blog.entity.User;
import com.blog.exceptions.InvalidPasswordException;
import com.blog.exceptions.UserBannedException;
import com.blog.exceptions.UserNotFoundException;
import com.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole("ROLE_USER");
        testUser.setStatus("ACTIVE");

        authRequest = new AuthRequest("testuser", "password123", "test@example.com");
    }

    @Test
    @DisplayName("Should successfully login with username")
    void testLoginSuccess_WithUsername() {
        // Arrange
        when(userRepository.findByUserName(authRequest.getUsername()))
                .thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername(testUser.getUserName()))
                .thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(true);
        when(passwordEncoder.matches(authRequest.getPassword(), testUser.getPassword()))
                .thenReturn(true);
        when(jwtUtils.generateToken(testUser.getUserName(), testUser.getRole()))
                .thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.login(authRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Login successful");
        assertThat(response.getUserRole()).isEqualTo("ROLE_USER");
        assertThat(response.getUserName()).isEqualTo("testuser");
        assertThat(response.getToken()).isEqualTo("jwt-token");

        verify(userRepository).findByUserName(authRequest.getUsername());
        verify(passwordEncoder).matches(authRequest.getPassword(), testUser.getPassword());
        verify(jwtUtils).generateToken(testUser.getUserName(), testUser.getRole());
    }

    @Test
    @DisplayName("Should successfully login with email")
    void testLoginSuccess_WithEmail() {
        // Arrange
        AuthRequest emailAuthRequest = new AuthRequest(null, "password123", "test@example.com");
        when(userRepository.findByUserName(null))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(emailAuthRequest.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername(testUser.getUserName()))
                .thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(true);
        when(passwordEncoder.matches(emailAuthRequest.getPassword(), testUser.getPassword()))
                .thenReturn(true);
        when(jwtUtils.generateToken(testUser.getUserName(), testUser.getRole()))
                .thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.login(emailAuthRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotNull();
        verify(userRepository).findByEmail(emailAuthRequest.getEmail());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user doesn't exist")
    void testLoginFail_UserNotFound() {
        // Arrange
        when(userRepository.findByUserName(anyString()))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(authRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findByUserName(authRequest.getUsername());
        verify(userRepository).findByEmail(authRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw InvalidPasswordException when password is incorrect")
    void testLoginFail_InvalidPassword() {
        // Arrange
        when(userRepository.findByUserName(authRequest.getUsername()))
                .thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername(testUser.getUserName()))
                .thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(true);
        when(passwordEncoder.matches(authRequest.getPassword(), testUser.getPassword()))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(authRequest))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Invalid password");

        verify(passwordEncoder).matches(authRequest.getPassword(), testUser.getPassword());
        verify(jwtUtils, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw UserBannedException when user is banned")
    void testLoginFail_UserBanned() {
        // Arrange
        when(userRepository.findByUserName(authRequest.getUsername()))
                .thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername(testUser.getUserName()))
                .thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(authRequest))
                .isInstanceOf(UserBannedException.class)
                .hasMessage("banned from logging");

        verify(userDetailsService).loadUserByUsername(testUser.getUserName());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}


