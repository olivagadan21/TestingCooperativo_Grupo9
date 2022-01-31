package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.AppException;
import com.sopromadze.blogapi.exception.BadRequestException;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    void checkUsernameAvailability_success() {

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
    void addUser_success() {

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setUsername("oliva.gadan21");
        user.setRoles(roleList);

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(roleRepository.findByName(rol.getName())).thenReturn(Optional.of(rol));
        when(userRepository.save(user)).thenReturn(user);

        assertEquals(user, userService.addUser(user));

    }

    @Test
    void addUser_BadRequestException_Username_success() {

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setUsername("oliva.gadan21");
        user.setRoles(roleList);

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.addUser(user));

    }

    @Test
    void addUser_BadRequestException_Email_success() {

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setUsername("oliva.gadan21");
        user.setRoles(roleList);

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.addUser(user));

    }

    @Test
    void addUser_AppException_success() {

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

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(roleRepository.findByName(rol.getName())).thenReturn(Optional.of(rol));
        when(userRepository.save(user)).thenReturn(user);

        assertThrows(AppException.class, () -> userService.addUser(user));

    }

    @Test
    void giveAdmin_success() {

        Role usuario = new Role();
        usuario.setName(RoleName.ROLE_USER);

        Role admin = new Role();
        admin.setName(RoleName.ROLE_ADMIN);

        List<Role> roleList = new ArrayList<>();
        roleList.add(usuario);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setUsername("oliva.gadan21");
        user.setRoles(roleList);

        ApiResponse apiResponse = new ApiResponse(true, "You gave ADMIN role to user: " + user.getUsername());

        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(roleRepository.findByName(admin.getName())).thenReturn(Optional.of(admin));
        when(roleRepository.findByName(usuario.getName())).thenReturn(Optional.of(usuario));
        when(userRepository.save(user)).thenReturn(user);

        assertEquals(apiResponse, userService.giveAdmin(user.getUsername()));

    }

    @Test
    void giveAdmin_AppException_Admin_success() {

        Role usuario = new Role();
        usuario.setName(RoleName.ROLE_USER);

        Role admin = new Role();
        admin.setName(RoleName.ROLE_USER);

        List<Role> roleList = new ArrayList<>();
        roleList.add(usuario);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setUsername("oliva.gadan21");
        user.setRoles(roleList);

        ApiResponse apiResponse = new ApiResponse(true, "You gave ADMIN role to user: " + user.getUsername());

        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(roleRepository.findByName(admin.getName())).thenReturn(Optional.of(admin));
        when(roleRepository.findByName(usuario.getName())).thenReturn(Optional.of(usuario));
        when(userRepository.save(user)).thenReturn(user);

        assertThrows(AppException.class, () -> userService.giveAdmin(user.getUsername()));

    }

    @Test
    void giveAdmin_AppException_User_success() {

        Role usuario = new Role();
        usuario.setName(RoleName.ROLE_ADMIN);

        Role admin = new Role();
        admin.setName(RoleName.ROLE_ADMIN);

        List<Role> roleList = new ArrayList<>();
        roleList.add(usuario);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setUsername("oliva.gadan21");
        user.setRoles(roleList);

        ApiResponse apiResponse = new ApiResponse(true, "You gave ADMIN role to user: " + user.getUsername());

        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(roleRepository.findByName(admin.getName())).thenReturn(Optional.of(admin));
        when(roleRepository.findByName(usuario.getName())).thenReturn(Optional.of(usuario));
        when(userRepository.save(user)).thenReturn(user);

        assertThrows(AppException.class, () -> userService.giveAdmin(user.getUsername()));

    }

}