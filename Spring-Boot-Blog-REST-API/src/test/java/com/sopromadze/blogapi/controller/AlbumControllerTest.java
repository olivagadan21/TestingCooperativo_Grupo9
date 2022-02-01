package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.AlbumServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT, classes =
        {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlbumServiceImpl albumService;

    @Test
    @WithUserDetails("USER")
    @DisplayName("Add album return 200")
    void addAlbum_success() throws Exception {

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(roleList);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setId(1L);
        albumRequest.setTitle("Viaje a Francia.");
        albumRequest.setUser(user);

        Album album = new Album();
        album.setId(albumRequest.getId());
        album.setTitle(albumRequest.getTitle());
        album.setUser(user);

        ResponseEntity<Album> responseEntity = new ResponseEntity<Album>(album, HttpStatus.CREATED);

        when(albumService.addAlbum(albumRequest, userPrincipal)).thenReturn(responseEntity);

        mockMvc.perform(post("/api/albums")
                        .content(objectMapper.writeValueAsString(album))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @DisplayName("Update album return 200")
    void updateAlbum_success() throws Exception {

        Role rol = new Role();
        rol.setId(1L);
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(roleList);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Viaje a Francia.");
        album.setUser(user);

        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setId(album.getId());
        albumRequest.setTitle("Viaje a París");
        albumRequest.setUser(user);

        Album updated = new Album();
        updated.setId(album.getId());
        updated.setTitle(albumRequest.getTitle());
        updated.setUser(albumRequest.getUser());

        AlbumResponse albumResponse = new AlbumResponse();
        albumResponse.setId(updated.getId());
        albumResponse.setTitle(updated.getTitle());
        albumResponse.setUser(updated.getUser());

        ResponseEntity<AlbumResponse> responseEntity = new ResponseEntity<AlbumResponse>(albumResponse, HttpStatus.OK);

        when(albumService.updateAlbum(album.getId(), albumRequest, userPrincipal)).thenReturn(responseEntity);

        mockMvc.perform(put("/api/albums/{id}", 1L)
                        .content(objectMapper.writeValueAsString(albumRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @DisplayName("delete album return 200")
    void deleteAlbum_success() throws Exception {

        Role admin = new Role();
        admin.setId(1L);
        admin.setName(RoleName.ROLE_ADMIN);
        List<Role> listRole = new ArrayList<>();
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

        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "You successfully deleted album");
        ResponseEntity<ApiResponse> responseResponseEntity = new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);

        when(albumService.deleteAlbum(album.getId(), userPrincipal)).thenReturn(responseResponseEntity);

        mockMvc.perform(delete("/api/albums/1", album.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());

    }
}