package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.UserServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    /*@WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @Test
    void whenUpdateUser_returns201() throws Exception {
        System.out.println(user);
        mockMvc.perform(put("/api/users/{username}", "DaTruth")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType("application/json"))
                .andExpect(status().isCreated()).andDo(print());
    }*/
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




}
