package com.connect.service;

import com.connect.dto.LoginUserDTO;
import com.connect.enums.UserStatus;
import com.connect.exception.DuplicateResourceException;
import com.connect.exception.TimeoutException;
import com.connect.exception.UserCreationException;
import com.connect.model.User;
import com.connect.repository.UserRepository;
import com.connect.security.CustomUserDetails;
import com.connect.utils.EmailUtil;
import com.connect.utils.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OTPService otpService;

    @Mock
    private EmailService emailService;

    @Mock
    private RedisService redisService;

    @Mock
    private EmailUtil emailUtil;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    public void testSignupHandler_WhenNewUser_ThenOpenSentAndCached() {
        User newUser = User.builder()
                .username("aditya")
                .email("adi@gmail.com")
                .password("aditya123")
                .build();

        // Mocking the repository
        Mockito.when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername(newUser.getUsername())).thenReturn(Optional.empty());

        // Mocking OTP generation
        Mockito.when(otpService.generateOTPForUser(newUser.getEmail())).thenReturn("1234");

        // Mocking the email body
        Mockito.when(emailUtil.getBody()).thenReturn("Hello %s, This is OTP, %s");

        // Doing nothing for side effects
        Mockito.doNothing().when(emailService).sendEmail(newUser.getEmail(), "One Time Password", "Hello aditya, This is OTP, 1234");

        Mockito.doNothing().when(redisService).cacheUserWithTTL(newUser);

        // Main act
        authService.signupHandler(newUser);

        // Verifying the behavior that the function run or not.
        Mockito.verify(userRepository).findByEmail(newUser.getEmail());
        Mockito.verify(userRepository).findByUsername(newUser.getUsername());
        Mockito.verify(otpService).generateOTPForUser(newUser.getEmail());
        Mockito.verify(emailUtil).getBody();
        Mockito.verify(emailService).sendEmail(newUser.getEmail(), "One Time Password", "Hello aditya, This is OTP, 1234");
        Mockito.verify(redisService).cacheUserWithTTL(newUser);
    }

    @Test
    public void testSignupHandler_withExistedEmail() {
        User user = User.builder()
                .username("aditya")
                .email("adi@gmail.com")
                .password("aditya123")
                .build();

        // Mocking the repository.
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // So as per the behavior of the method if the user found it will throws up a DuplicateResourceException.
        // act
        Assertions.assertThrows(DuplicateResourceException.class, () -> {
            authService.signupHandler(user);
        });
    }

    @Test
    public void testSignupHandler_withExistedUsername() {
        User user = User.builder()
                .username("aditya")
                .email("adi@gmail.com")
                .password("aditya123")
                .build();

        // Note: Still you are testing for the username you have to mock the email too.
        // Cause this is the flow of the method otherwise it will lead to failure situations.
        // When we didn't mock the email in that case mockito will return null so it didn't throw the exception.

        // Mocking email
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // act
        Assertions.assertThrows(DuplicateResourceException.class, () -> {
            authService.signupHandler(user);
        });
    }

    @Test
    public void testResendOTP_WhenValidEmail_ThenOTPRemovedFromCache() {

        String email = "adi@gmail.com";

        // Mocking Redis Service
        Mockito.when(redisService.getUser(email))
                .thenReturn(User.builder()
                        .username("aditya")
                        .email(email).
                        password("aditya123")
                        .build());

        // Do nothing for side effects
        Mockito.when(redisService.removeOTP(email)).thenReturn(true);

        // Mocking the OTP
        Mockito.when(otpService.generateOTPForUser(email)).thenReturn("1234");

        // Mocking the email util
        Mockito.when(emailUtil.getBody()).thenReturn("Hey %s, This is OTP, %s");

        // Mocking the email service
        Mockito.doNothing().when(emailService).sendEmail(email, "One Time Password", "Hey aditya, This is OTP, 1234");

        // Main act
        authService.resendOTP(email);

        // Verifying mocks
        Mockito.verify(redisService).getUser(email);
        Mockito.verify(redisService).removeOTP(email);
        Mockito.verify(otpService).generateOTPForUser(email);
        Mockito.verify(emailUtil).getBody();
        Mockito.verify(emailService).sendEmail(email, "One Time Password", "Hey aditya, This is OTP, 1234");
    }

    @Test
    public void testResendOTP_withInvalidEmail() {
        String email = "adi@gmail.com";

        Mockito.when(redisService.getUser(email)).thenReturn(null);

        // Act
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            authService.resendOTP(email);
        });

        Assertions.assertEquals("Email doesn't exists, Bad Request!", exception.getMessage());
    }
    
    @Test
    public void testCreateUser_withValidOTP() {
        String email = "adi@gmail.com";
        String otp = "1234";

        User user = User.builder()
                .username("aditya")
                .email(email)
                .password("aditya123").build();

        Mockito.when(otpService.verifyOTP(email, otp)).thenReturn(true);

        // Mocking the remove otp function.
        Mockito.when(redisService.removeOTP(email)).thenReturn(true);

        // Mocking the getUser() function.
        Mockito.when(redisService.getUser(email)).thenReturn(user);

        Mockito.when(userRepository.createUser(user)).thenReturn(Optional.of(user));

        Mockito.when(redisService.removeUser(email)).thenReturn(true);

        // Act
        Assertions.assertNotNull(authService.createUser(otp, email));

        // Verifying the mocks
        Mockito.verify(otpService).verifyOTP(email, otp);
        Mockito.verify(redisService).removeOTP(email);
        Mockito.verify(redisService).getUser(email);
        Mockito.verify(userRepository).createUser(user);
        Mockito.verify(redisService).removeUser(email);
    }

    @Test
    public void testCreateUser_withInvalidOTP() {

        Mockito.when(otpService.verifyOTP("adi@gmail.com", "1234")).thenReturn(false);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            authService.createUser("1234", "adi@gmail.com");
        });

        Assertions.assertEquals("Invalid OTP", exception.getMessage());
    }

    @Test
    public void testCreateUser_withValidOTP_ThenUserNotFoundInCache() {
        String email = "adi@gmail.com";
        String otp = "1234";

        Mockito.when(otpService.verifyOTP(email, otp)).thenReturn(true);

        // Mocking the remove otp function.
        Mockito.when(redisService.removeOTP(email)).thenReturn(true);

        Mockito.when(redisService.getUser(email)).thenReturn(null);

        TimeoutException exception = Assertions.assertThrows(TimeoutException.class, () -> {
            authService.createUser(otp, email);
        });

        Assertions.assertEquals("User not found in the cache, Try again later", exception.getMessage());
    }

    @Test
    public void testCreateUser_withValidOTP_ThenFailedToSaveUser() {
        String email = "adi@gmail.com";
        String otp = "1234";

        User user = User.builder()
                .username("aditya")
                .email(email)
                .password("aditya123").build();

        Mockito.when(otpService.verifyOTP(email, otp)).thenReturn(true);

        // Mocking the remove otp function.
        Mockito.when(redisService.removeOTP(email)).thenReturn(true);

        // Mocking the getUser() function.
        Mockito.when(redisService.getUser(email)).thenReturn(user);

        Mockito.when(userRepository.createUser(user)).thenReturn(Optional.empty());

        UserCreationException exception = Assertions.assertThrows(UserCreationException.class, () -> {
            authService.createUser(otp, email);
        });

        Assertions.assertEquals("Something went wrong at the server! try again later.", exception.getMessage());
    }

    @Test
    public void testLoginHandler_WhenCredentialsValid_ThenReturnTokenAndExpiry() throws Exception {
        User user = User.builder()
                .username("aditya")
                .email("adi@gmail.com")
                .password("aditya123")
                .build();
        String token = "auth-token";
        Date expiryDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60);

        // Mocking user details
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // Mocking authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(customUserDetails);

        Mockito.when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        )).thenReturn(authentication);

        // Mocking JWT Utility classes
        Mockito.when(jwtUtil.generateToken(user.getUsername(), user.getEmail())).thenReturn(token);
        Mockito.when(jwtUtil.getExpirationDate(token)).thenReturn(expiryDate);

        Mockito.when(userRepository.updateUserStatus(user.getUsername(), UserStatus.ACTIVE)).thenReturn(CompletableFuture.completedFuture(Optional.of(user)));

        // Act
        Map<String, Object> result = authService.loginHandler(
                LoginUserDTO.builder()
                        .email(user.getEmail())
                        .password(user.getPassword()).build());

        Assertions.assertEquals(token, result.get("token"));
        Assertions.assertEquals(expiryDate, result.get("expiresAt"));

        // Verify

        Mockito.verify(authenticationManager).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(jwtUtil).generateToken(user.getUsername(), user.getEmail());
        Mockito.verify(jwtUtil).getExpirationDate(token);
        Mockito.verify(userRepository).updateUserStatus(user.getUsername(), UserStatus.ACTIVE);
    }

    @Test
    public void testLoginHandler_WhenCredentialsInvalid_ThenThrowException() {
        LoginUserDTO user = LoginUserDTO.builder()
                .email("wrongemail@gmail.com")
                .password("wrongpass")
                .build();
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenThrow(new BadCredentialsException("Bad Credentials"));

        Exception exception = Assertions.assertThrows(BadCredentialsException.class, () -> {
            authService.loginHandler(user);
        });

        Assertions.assertEquals("Bad Credentials", exception.getMessage());
    }

}
