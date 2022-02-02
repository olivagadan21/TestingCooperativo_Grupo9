package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.*;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.Geo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.*;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
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
