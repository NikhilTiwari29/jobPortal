package com.jobPortal;

import com.jobPortal.dto.LoginDTO;
import com.jobPortal.dto.UserDTO;
import com.jobPortal.entity.User;
import com.jobPortal.enums.AccountType;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.repository.UserRepository;
import com.jobPortal.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO userDto;

    private LoginDTO loginDto;

    @BeforeEach
    void setUp(){
        userDto = UserDTO.builder()
                .userName("Nikhil")
                .email("nikhiltiwarp29@gmail.com")
                .password("NikTiwari@1234")
                .accountType(AccountType.EMPLOYER)
                .build();

        loginDto = LoginDTO.builder()
                .email("nikhiltiwarp29@gmail.com")
                .password("NikTiwari@1234")
                .build();
    }

    @Test
    void registerUser_userAlreadyExistsWithEmail_throwsException() {
//        ARRANGE
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);
//        ACT & ASSERT
        assertThatThrownBy(() -> userService.registerUser(userDto))
                .isInstanceOf(JobPortalException.class)
                .hasMessage("user.already.exists");


//        verify
        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void registerUser_success() throws JobPortalException {
        // ARRANGE
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);

        User savedUser = User.builder()
                .userName(userDto.getUserName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .accountType(userDto.getAccountType())
                .build();

        when(modelMapper.map(userDto, User.class)).thenReturn(savedUser);
        when(userRepository.save(savedUser)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserDTO.class)).thenReturn(userDto);

        // ACT
        UserDTO result = userService.registerUser(userDto);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(userDto.getEmail());

        // VERIFY
        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());
        verify(userRepository, times(1)).save(savedUser);
    }

    @Test
    void loginUser_userDoesNotExistWithEmail_throwsException(){
        //        ARRANGE

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> userService.loginUser(loginDto))
                .isInstanceOf(JobPortalException.class)
                .hasMessage("user.does.not.exists");

        //verify

        verify(userRepository,times(1)).findByEmail(loginDto.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void loginUser_invalidPassword_throwsException() {
        // ARRANGE

        User user = User.builder()
                .userName(userDto.getUserName())
                .email(userDto.getEmail())
                .password("encodedPassword")
                .accountType(userDto.getAccountType())
                .build();

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> userService.loginUser(loginDto))
                .isInstanceOf(JobPortalException.class)
                .hasMessage("user.invalid.credentials");

        // VERIFY
        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginDto.getPassword(), user.getPassword());
        verify(modelMapper, never()).map(any(User.class), eq(UserDTO.class));
    }

    @Test
    void loginUser_SuccessFull() throws JobPortalException {
        // ARRANGE
        User user = User.builder()
                .userName(userDto.getUserName())
                .email(userDto.getEmail())
                .password("encodedPassword")
                .accountType(userDto.getAccountType())
                .build();

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDto);

        // ACT
        UserDTO response = userService.loginUser(loginDto);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(loginDto.getEmail());

        // VERIFY
        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginDto.getPassword(), user.getPassword());
        verify(modelMapper, times(1)).map(user, UserDTO.class);
    }
}
