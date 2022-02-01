package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsServiceImpl customUserDetailsService;

    Role rol;
    Role rol2;
    User user;

    UserPrincipal userPrincipal;
    @BeforeEach
    void initData(){

        rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);
        rol2 = new Role();
        rol2.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol, rol2);

        user = new User();
        user.setUsername("DaTruth");
        user.setRoles(roles);

        userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

    }

    @Test
    void loadUserById_success(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertEquals(customUserDetailsService.loadUserById(user.getId()), userPrincipal);


    }

    /*
     * Test: Se comprueba que el método devuelve UserDetails
     * Entrada: customUserDetailsService.loadUserByUsername("Jesusito")
     * Salida esperada: Test se realiza con éxito y devuelve el UserDetails indicado por nombre
     */
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

    /*
     * Test: Se comprueba que el método lanza la expepción UsernameNotFoundException
     * Entrada: customUserDetailsService.loadUserByUsername("barco#y")
     * Salida esperada: Test se realiza con éxito y lanza la excepción UsernameNotFoundException
     */
    @Test
    @DisplayName("Load by username username not found exception")
    void loadUserByUsername_UsernameNotFoundException() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,()-> customUserDetailsService.loadUserByUsername("barco#y"));
    }

}
