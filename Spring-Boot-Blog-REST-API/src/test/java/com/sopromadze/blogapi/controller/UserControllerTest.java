package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestConfig;
import com.sopromadze.blogapi.payload.InfoRequest;
import com.sopromadze.blogapi.payload.UserProfile;
import com.sopromadze.blogapi.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT, classes =
        {SpringSecurityTestConfig.class})
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserServiceImpl userService;

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
}