package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.service.PhotoService;
import com.sopromadze.blogapi.service.impl.AlbumServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class AlbumControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AlbumServiceImpl albumService;

    @MockBean
    PhotoService photoService;


    private Album album;
    private ResponseEntity<Album> responseEntity;

    @BeforeEach
    void initData(){
        album = new Album();
        album.setId(1L);

        responseEntity = new ResponseEntity<>(album, HttpStatus.OK);

    }

    @Test
    void whenGetAlbum_returns200() throws Exception {
        when(albumService.getAlbum(1L)).thenReturn(responseEntity);
        mockMvc.perform(get("/api/albums/{id}", 1L))
                .andExpect(status().isOk()).andDo(print());

    }

    @Test
    @DisplayName("Get all albums return 200")
    void getAllAlbums_success() throws Exception {
        AlbumResponse albumResponse = new AlbumResponse();
        albumResponse.setId(1l);

        List<AlbumResponse> albumResponses = Arrays.asList(albumResponse);

        PagedResponse<AlbumResponse> result = new PagedResponse<>();
        result.setContent(albumResponses);
        result.setTotalPages(1);
        result.setTotalElements(1);
        result.setLast(true);
        result.setSize(1);

        when(albumService.getAllAlbums(1,1)).thenReturn(result);
        mockMvc.perform(get("/api/albums")
                        .param("size","1").param("page","1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Get all photos by album return 200")
    void getAllPhotosByAlbum_success() throws Exception{
        Album album = new Album();
        album.setId(1L);

        Photo photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Mi foto en la playa");
        photo.setAlbum(album);

        PhotoResponse photoResponse = new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(),
                photo.getThumbnailUrl(), photo.getAlbum().getId());

        Page<PhotoResponse> photosPage = new PageImpl<>(Arrays.asList(photoResponse));

        PagedResponse<PhotoResponse> photoPagedResponse = new PagedResponse<>();
        photoPagedResponse.setContent(photosPage.getContent());
        photoPagedResponse.setTotalElements(1);
        photoPagedResponse.setLast(true);
        photoPagedResponse.setSize(1);
        photoPagedResponse.setTotalPages(1);

        when(photoService.getAllPhotosByAlbum(1L,1,1)).thenReturn(photoPagedResponse);
        mockMvc.perform(get("/api/albums/{id}/photos",1L)
                        .param("size","1").param("page","1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(photoPagedResponse)))
                .andExpect(status().isOk());
    }


}
