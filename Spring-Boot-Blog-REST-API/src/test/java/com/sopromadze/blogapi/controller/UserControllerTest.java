package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.InfoRequest;
import com.sopromadze.blogapi.payload.UserProfile;
import com.sopromadze.blogapi.service.impl.UserServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserServiceImpl userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    Role rol;
    Role rol2;
    User user;

    @BeforeEach
    void initData(){

        user = new User();
        user.setId(3L);
        user.setUsername("DaTruth");
        user.setFirstName("Vicente");
        user.setLastName("Rufo");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setEmail("pepe@gmail.com");
        user.setPhone("625971527");
        user.setWebsite("www.jrwtf.es");
        user.setCreatedAt(Instant.now());


    }

    @WithMockUser("user")
    @Test
    void whenGetCurrentUser_returns200() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void whenGetCurrentUser_returns401() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    void whenCheckEmailAvailability_returns200() throws Exception {

        mockMvc.perform(get("/api/users/checkEmailAvailability", String.class)
                        .param("email", "pepe@gmail.com")
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @Test
    void whenUpdateUser_returns201() throws Exception {
        System.out.println(user);
        mockMvc.perform(put("/api/users/{username}", "DaTruth")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType("application/json"))
                .andExpect(status().isCreated()).andDo(print());
    }
    @Test
    void whenUpdateUser_returns401() throws Exception {
        System.out.println(user);
        mockMvc.perform(put("/api/users/{username}", "DaTruth")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @WithMockUser(authorities = {"ROLE_ADMIN"})
    @Test
    void whenTakeAdmin_returns200() throws Exception {
        System.out.println(user);
        mockMvc.perform(put("/api/users/{username}/takeAdmin", user.getUsername())
                        .content(objectMapper.writeValueAsString(user))
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void whenTakeAdmin_returns401() throws Exception {
        System.out.println(user);
        mockMvc.perform(put("/api/users/{username}/takeAdmin", user.getUsername())
                        .content(objectMapper.writeValueAsString(user))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }


    @Test
    @DisplayName("Get user profile return 200")
    void getUSerProfile_success() throws Exception{

        UserProfile userProfile = new UserProfile();
        userProfile.setId(1L);
        userProfile.setUsername("Jesús");

        when(userService.getUserProfile("Jesús")).thenReturn(userProfile);
        mockMvc.perform(get("/api/users/{username}/profile", "Jesús")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userProfile)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete user return 200")
    @WithUserDetails("ADMIN")
    void deleteUser_success() throws Exception {
        mockMvc.perform(delete("/api/users/{username}", "Jesús")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete user unauthorized return 401")
    void deleteUser_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/users/{username}", "Jesús")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Set address return 200")
    @WithUserDetails("ADMIN")
    void setAddress_success() throws Exception{
        InfoRequest infoRequest = new InfoRequest();
        infoRequest.setLat("37.3827100");
        infoRequest.setLng("-6.0025700");
        infoRequest.setStreet("Puente de triana");
        infoRequest.setSuite("Puente");
        infoRequest.setCity("Sevilla");
        infoRequest.setZipcode("41001");

        mockMvc.perform(put("/api/users/setOrUpdateInfo")
                        .content(objectMapper.writeValueAsString(infoRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Set address unauthorized return 401")
    void setAddress_Unauthorized() throws Exception{
        InfoRequest infoRequest = new InfoRequest();

        mockMvc.perform(put("/api/users/setOrUpdateInfo")
                        .content(objectMapper.writeValueAsString(infoRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Set address return 400")
    @WithUserDetails("ADMIN")
    void setAddress_badRequest() throws Exception{
        InfoRequest infoRequest = new InfoRequest();
        UserProfile userProfile = new UserProfile();

        mockMvc.perform(put("/api/users/setOrUpdateInfo")
                        .content(objectMapper.writeValueAsString(userProfile))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("ADMIN")
    @DisplayName("Add album return 200")
    void addUser_success() throws Exception {

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva2@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setUsername("oliva.gadan2");
        user.setRoles(roleList);

        when(userService.addUser(user)).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    @DisplayName("CheckUsernameAvailability return 200")
    void checkUsernameAvailability_success() throws Exception {

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

        mockMvc.perform(get("/api/users/checkUsernameAvailability", String.class)
                        .param("username", user.getUsername())
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());

    }

    @Test
    @WithUserDetails("ADMIN")
    @DisplayName("GiveAdmin return 200")
    void giveAdmin_success() throws Exception {

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

        when(userService.giveAdmin(user.getUsername())).thenReturn(apiResponse);

        mockMvc.perform(put("/api/users/oliva.gadan21/takeAdmin", 1L)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}
