package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.UserIdentityAvailability;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void checkUsernameAvailability_access() {

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setUsername("oliva.gadan21");
        user.setRoles(roleList);

        UserIdentityAvailability userIdentityAvailability = new UserIdentityAvailability(userService.checkUsernameAvailability(user.getUsername()).getAvailable());

        when(!userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        assertTrue(userIdentityAvailability.getAvailable());

    }

    @Test
    void addUser() {
    }

    @Test
    void giveAdmin() {
    }
}