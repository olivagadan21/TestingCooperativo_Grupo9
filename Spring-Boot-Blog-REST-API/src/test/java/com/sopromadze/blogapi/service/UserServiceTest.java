package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.exception.AppException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.UserIdentityAvailability;
import com.sopromadze.blogapi.payload.UserSummary;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    UserSummary userSummary;
    Role rol;
    Role rol2;
    User user2;
    UserPrincipal userPrincipal;
    User user;
    UserPrincipal userPrincipal2;
    UserIdentityAvailability userIdentityAvailability;
    Address address;
    Company company;
    @BeforeEach
    void initData(){

        rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);
        rol2 = new Role();
        rol2.setName(RoleName.ROLE_USER);

        List<Role> roles1 = Arrays.asList(rol);

        List<Role> roles2 = Arrays.asList(rol2);

        address = new Address();
        address.setId(4L);

        company = new Company();
        company.setId(5L);

        user = new User();
        user.setId(3L);
        user.setUsername("DaTruth");
        user.setFirstName("Vicente");
        user.setLastName("Rufo");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setAddress(address);
        user.setPhone("625971527");
        user.setWebsite("www.jrwtf.es");
        user.setCompany(company);
        user.setRoles(roles1);
        user.setCreatedAt(Instant.now());

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("Nanogenix");
        user2.setRoles(roles2);
        user2.setCreatedAt(Instant.now());

        userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName("Alejandro")
                .lastName("Cuevas")
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        userPrincipal2 = UserPrincipal.builder()
                .id(user2.getId())
                .username(user2.getUsername())
                .authorities(user2.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        userSummary = new UserSummary(userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getFirstName(), userPrincipal.getLastName());



        userIdentityAvailability = new UserIdentityAvailability(true);

    }

    @Test
    void getCurrentUser_success(){
        assertEquals(userService.getCurrentUser(userPrincipal), userSummary);
    }

    @Test
    void checkEmailAvailability_success(){
        when(userRepository.existsByEmail("pepe@gmail")).thenReturn(false);
        assertEquals(userService.checkEmailAvailability("pepe@gmail.com"), userIdentityAvailability);
    }

    @Test
    void updateUser_success(){
        when(userRepository.getUserByName(userSummary.getUsername())).thenReturn(user);
        when(passwordEncoder.encode("1234")).thenReturn("1234");
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(userRepository.save(user), user);
    }

    @Test
    void updateUser_throwsUnauthorizedException(){
        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(passwordEncoder.encode("1234")).thenReturn("1234");
        when(userRepository.save(user)).thenReturn(user);
        assertThrows(UnauthorizedException.class, ()->userService.updateUser(user, user.getUsername(), userPrincipal2));
    }

    @Test
    void removeAdmin_success(){
        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(rol2));
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(userService.removeAdmin(user.getUsername()), new ApiResponse(Boolean.TRUE, "You took ADMIN role from user: " + user.getUsername()));
    }

    @Test
    void removeAdmin_throwsAppExceptionForRole(){
        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(roleRepository.findByName(null)).thenReturn(Optional.empty());
        assertThrows(AppException.class, ()->userService.removeAdmin(user.getUsername()));
    }


}
