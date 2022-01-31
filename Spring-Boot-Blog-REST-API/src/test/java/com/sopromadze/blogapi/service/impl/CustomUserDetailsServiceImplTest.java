package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomUserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsServiceImpl customUserDetailsService;

    @Test
    @DisplayName("Load by username")
    void loadUserByUsername_succes() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
        List<Role> listRole = new ArrayList<>();
        listRole.add(role);

        User user = new User();
        user.setId(1L);
        user.setUpdatedAt(Instant.now());
        user.setEmail("jesus@gmail.com");
        user.setPassword("12345678");
        user.setUsername("Jesusito");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));
        assertEquals(user.getUsername(),customUserDetailsService.loadUserByUsername("Jesusito").getUsername());

    }

    @Test
    @DisplayName("Load by username usernameNotFoundException")
    void loadUserByUsername_UsernameNotFoundException() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,()-> customUserDetailsService.loadUserByUsername("barco#y"));
    }
}