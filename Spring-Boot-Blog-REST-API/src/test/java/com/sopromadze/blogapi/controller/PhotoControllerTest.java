package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestConfig;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.service.PhotoService;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT, classes =
        {SpringSecurityTestConfig.class})
@AutoConfigureMockMvc
class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Add photo return 201")
    @WithUserDetails("USER")
    void addPhoto_success() throws Exception {
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Solicitud de fotos");
        photoRequest.setUrl("https://photoreques");
        photoRequest.setThumbnailUrl("https://photoRequest");
        photoRequest.setAlbumId(1L);

        mockMvc.perform(post("/api/photos")
                        .content(objectMapper.writeValueAsString(photoRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Add photo unauthorized return 401")
    void addPhoto_Unauthorized() throws Exception {
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Solicitud de fotos");
        photoRequest.setUrl("https://photoreques");
        photoRequest.setThumbnailUrl("https://photoRequest");
        photoRequest.setAlbumId(1L);

        mockMvc.perform(post("/api/photos")
                        .content(objectMapper.writeValueAsString(photoRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Add photo return 400")
    @WithUserDetails("USER")
    void addPhoto_badRequest() throws Exception {
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Solicitud de fotos");
        photoRequest.setUrl("https://photoreques");
        photoRequest.setThumbnailUrl("https://photoRequest");
        photoRequest.setAlbumId(1L);
        User user = new User();
        mockMvc.perform(post("/api/photos")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}