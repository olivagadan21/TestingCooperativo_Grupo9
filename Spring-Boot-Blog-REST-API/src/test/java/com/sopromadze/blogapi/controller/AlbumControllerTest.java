package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.service.impl.AlbumServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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



}
