package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.AccessDeniedException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.Geo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.InfoRequest;
import com.sopromadze.blogapi.payload.UserProfile;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    UserServiceImpl userService;
    /*
     * Test: Se comprueba que el método devuelve UserProfile
     * Entrada: userService.getUserProfile("Jesús")
     * Salida esperada: Test se realiza con éxito
     */
    @Test
    @DisplayName("Get user profile")
    void getUserProfile_success() {
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
        user.setFirstName("Jesús");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        post.setUser(user);

        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(),
                user.getCreatedAt(), user.getEmail(), user.getAddress(), user.getPhone(), user.getWebsite(),
                user.getCompany(), user.getId());


        when(userRepository.getUserByName("Jesús")).thenReturn(user);
        when(postRepository.countByCreatedBy(user.getId())).thenReturn(user.getId());
        assertEquals(userProfile, userService.getUserProfile("Jesús"));
    }
    /*
     * Test: Se comprueba que el método devuelve ApiResponse
     * Entrada: userService.deleteUser("Jesús",userPrincipal)
     * Salida esperada: Test se realiza con éxito
     */
    @Test
    @DisplayName("Delete user")
    void deleteUser_success() {
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
        user.setFirstName("Jesús");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        UserPrincipal userPrincipal  = UserPrincipal.create(user);

        when(userRepository.findByUsername("Jesús")).thenReturn(Optional.of(user));
        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "You successfully deleted profile of: " + user.getFirstName());
        assertEquals(apiResponse, userService.deleteUser("Jesús",userPrincipal));
    }

    /*
     * Test: Se comprueba que el método lanza la excepción ResourceNotFoundException
     * Entrada: userService.deleteUser("Jesús",userPrincipal)
     * Salida esperada: Test se realiza con éxito y lanza la excepción ResourceNotFoundException
     */
    @Test
    @DisplayName("Delete user resource not found exception")
    void deleteUser_ResourceNotFoundException() {

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
        user.setFirstName("Jesús");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        UserPrincipal userPrincipal  = UserPrincipal.create(user);
        when(userRepository.findByUsername("Paco")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()-> userService.deleteUser("Jesús",userPrincipal));
    }
    /*
     * Test: Se comprueba que el método lanza la excepción AccessDeniedException
     * Entrada: userService.deleteUser("Jesús",userPrincipal)
     * Salida esperada: Test se realiza con éxito y lanza la excepción AccessDeniedException
     */
    @Test
    @DisplayName("Delete user access denied exception")
    void deleteUser_AccessDeniedException() {

        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);
        List<Role> listRole = new ArrayList<>();
        listRole.add(role);

        User user = new User();
        user.setId(1L);
        user.setUpdatedAt(Instant.now());
        user.setEmail("jesus@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Jesús");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        UserPrincipal userPrincipal  = UserPrincipal.create(user);
        when(userRepository.findByUsername("Jesús")).thenReturn(Optional.of(user));
        assertThrows(AccessDeniedException.class, ()-> userService.deleteUser("Jesús",userPrincipal));
    }

    /*
     * Test: Se comprueba que el método devuelve UserProfile
     * Entrada: userService.setOrUpdateInfo(userPrincipal, infoRequest)
     * Salida esperada: Test se realiza con éxito
     */
    @Test
    @DisplayName("Set or update info")
    void setOrUpdateInfo_success() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);
        List<Role> listRole = new ArrayList<>();
        listRole.add(role);

        User user = new User();
        user.setId(1L);
        user.setUpdatedAt(Instant.now());
        user.setEmail("jesus@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Jesús");
        user.setUsername("Jesús");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        InfoRequest infoRequest = new InfoRequest();
        infoRequest.setLat("37.3827100");
        infoRequest.setLng("-6.0025700");
        infoRequest.setStreet("Puente de triana");
        infoRequest.setSuite("Puente");
        infoRequest.setCity("Sevilla");
        infoRequest.setZipcode("41001");

        Geo geo = new Geo();
        geo.setLat(infoRequest.getLat());
        geo.setLng(infoRequest.getLng());

        Address address = new Address(infoRequest.getStreet(), infoRequest.getSuite(), infoRequest.getCity(),
                infoRequest.getZipcode(), geo);

        Company company = new Company();

        user.setAddress(address);
        user.setCompany(company);
        user.setWebsite(infoRequest.getWebsite());
        user.setPhone(infoRequest.getPhone());

        when(userRepository.findByUsername("Jesús")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(postRepository.countByCreatedBy(user.getId())).thenReturn(user.getId());

        UserProfile userProfile =  new UserProfile(user.getId(), user.getUsername(),
                user.getFirstName(), user.getLastName(), user.getCreatedAt(),
                user.getEmail(), user.getAddress(), user.getPhone(), user.getWebsite(),
                user.getCompany(), postRepository.countByCreatedBy(user.getId()));

        assertEquals(userProfile, userService.setOrUpdateInfo(userPrincipal, infoRequest));

    }
    /*
     * Test: Se comprueba que el método lanza la excepción ResourceNotFoundException
     * Entrada: userService.setOrUpdateInfo(userPrincipal, infoRequest)
     * Salida esperada: Test se realiza con éxito y lanza la excepción ResourceNotFoundException
     */
    @Test
    @DisplayName("Set or update info, resource not found")
    void setOrUpdateInfo_resourceNotFoundException() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);
        List<Role> listRole = new ArrayList<>();
        listRole.add(role);

        User user = new User();
        user.setId(1L);
        user.setUpdatedAt(Instant.now());
        user.setEmail("jesus@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Jesús");
        user.setUsername("Jesús");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        InfoRequest infoRequest = new InfoRequest();

        when(userRepository.findByUsername("Paco")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->userService.setOrUpdateInfo(userPrincipal,infoRequest));
    }
    /*
     * Test: Se comprueba que el método lanza la excepción AccessDeniedException
     * Entrada: userService.setOrUpdateInfo(userPrincipal, infoRequest)
     * Salida esperada: Test se realiza con éxito y lanza la excepción AccessDeniedException
     */
    @Test
    @DisplayName("Set or update info acccess denied")
    void setOrUpdateInfo_AccessDeniedException() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);
        List<Role> listRole = new ArrayList<>();
        listRole.add(role);

        User user = new User();
        user.setId(1L);
        user.setUpdatedAt(Instant.now());
        user.setEmail("jesus@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Jesús");
        user.setUsername("Jesús");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setUpdatedAt(Instant.now());
        secondUser.setEmail("jesus@gmail.com");
        secondUser.setPassword("12345678");
        secondUser.setFirstName("Jesús");
        secondUser.setUsername("Jesús");
        secondUser.setCreatedAt(Instant.now());
        secondUser.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(secondUser);

        InfoRequest infoRequest = new InfoRequest();
        infoRequest.setLat("37.3827100");
        infoRequest.setLng("-6.0025700");
        infoRequest.setStreet("Puente de triana");
        infoRequest.setSuite("Puente");
        infoRequest.setCity("Sevilla");
        infoRequest.setZipcode("41001");

        when(userRepository.findByUsername("Jesús")).thenReturn(Optional.of(user));
        assertThrows(AccessDeniedException.class, ()-> userService.setOrUpdateInfo(userPrincipal, infoRequest));
    }


}