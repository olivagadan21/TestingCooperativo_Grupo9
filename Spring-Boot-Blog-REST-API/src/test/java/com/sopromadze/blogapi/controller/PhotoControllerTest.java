package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.PhotoServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class PhotoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PhotoServiceImpl photoService;

    private Photo photo;
    private PhotoResponse photoResponse;
    private Album album;
    private PhotoRequest photoRequest;
    private Role rol;
    private User user;
    private UserPrincipal userPrincipal;


    @BeforeEach
    void initData() {

        album = new Album();
        album.setTitle("??lbum Reshulon");
        album.setId(2L);
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Foto chula");
        photo.setUrl("https://fotochula");
        photo.setThumbnailUrl("https://fotomuychula");
        photo.setAlbum(album);

        photoResponse = new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(), photo.getThumbnailUrl(), photo.getAlbum().getId());

        photoRequest = new PhotoRequest();
        photoRequest.setAlbumId(album.getId());
        photoRequest.setTitle("Foto muy chula");
        photoRequest.setUrl("https://fotosuperchula");
        photoRequest.setThumbnailUrl("https://fotosupermegachula");

        rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        user = new User();
        user.setId(3L);
        user.setRoles(roles);

        userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();



    }

    @Test
    void whenGetPhoto_returns200() throws Exception {

        System.out.println(photoService);

        when(photoService.getPhoto(photo.getId())).thenReturn(photoResponse);
        mockMvc.perform(get("/api/photos/{id}", 1L).contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());

    }

    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @Test
    void whenUpdatePhoto_returns200() throws Exception {

        when(photoService.updatePhoto(photo.getId(), photoRequest, userPrincipal)).thenReturn(photoResponse);
        mockMvc.perform(put("/api/photos/{id}", photo.getId())
                .content(objectMapper.writeValueAsString(photoRequest))
                .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());


    }

    @Test
    void whenUpdatePhoto_returns401() throws Exception{

        when(photoService.updatePhoto(photo.getId(), photoRequest, userPrincipal)).thenReturn(photoResponse);
        mockMvc.perform(put("/api/photos/{id}", photo.getId())
                        .content(objectMapper.writeValueAsString(photoRequest))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());

    }

    @Test
    @DisplayName("Add photo return 201")
    @WithUserDetails("USER")
    void addPhoto_success() throws Exception {
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Nueva foto");
        photoRequest.setUrl("https://photorequest.es");
        photoRequest.setThumbnailUrl("https://photoRequest.es");
        photoRequest.setAlbumId(1L);

        mockMvc.perform(post("/api/photos")
                        .content(objectMapper.writeValueAsString(photoRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Add photo unauthorized return 401")
    void addPhoto_Unauthorized() throws Exception {
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Solicitud de fotos");
        photoRequest.setUrl("https://photorequest.es");
        photoRequest.setThumbnailUrl("https://photoRequest.es");
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
        photoRequest.setUrl("https://photorequest.es");
        photoRequest.setThumbnailUrl("https://photoRequest.es");
        photoRequest.setAlbumId(1L);
        User user = new User();
        mockMvc.perform(post("/api/photos")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @DisplayName("delete photo return 200")
    void deletePhoto_success() throws Exception {

        Role admin = new Role();
        admin.setId(1L);
        admin.setName(RoleName.ROLE_ADMIN);
        List<Role> listRole = new ArrayList<>();
        listRole.add(admin);

        Role usuario = new Role();
        usuario.setId(2L);
        usuario.setName(RoleName.ROLE_USER);
        listRole.add(admin);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Viaje de fin de curso");
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());
        album.setUser(user);

        Photo photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Torre Eiffel");
        photo.setUrl("https://media.tacdn.com/media/attractions-splice-spp-674x446/06/74/ab/3e.jpg");
        photo.setThumbnailUrl("https://www.toureiffel.paris/sites/default/files/styles/1200x675/public/actualite/image_principale/IMG_20200526_123909.jpg?itok=DeDSW4xL");
        photo.setAlbum(album);

        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "Photo deleted successfully");

        when(photoService.deletePhoto(photo.getId(), userPrincipal)).thenReturn(apiResponse);

        mockMvc.perform(delete("/api/photos/1", photo.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());

    }

}
