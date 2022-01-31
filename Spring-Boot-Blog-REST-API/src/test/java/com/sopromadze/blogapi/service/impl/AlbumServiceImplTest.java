package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AlbumServiceImplTest {

    @Mock
    AlbumRepository albumRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    AlbumServiceImpl albumService;

    @Test
    @DisplayName("Get all albums")
    void getAllAlbums_success() {

        Album album = new Album();
        album.setTitle("Albúm Jesús");
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        Page<Album> albumPage = new PageImpl<>(Arrays.asList(album));

        AlbumResponse albumResponse = new AlbumResponse();
        albumResponse.setId(1l);

        AlbumResponse[] albumList = {albumResponse};

        List<AlbumResponse> albumResponses = Arrays.asList(albumResponse);

        PagedResponse<AlbumResponse> result = new PagedResponse<>();
        result.setContent(albumResponses);
        result.setTotalPages(1);
        result.setTotalElements(1);
        result.setLast(true);
        result.setSize(1);

        when(albumRepository.findAll((any(Pageable.class)))).thenReturn(albumPage);
        when(modelMapper.map(any(), any())).thenReturn(albumList);
        assertEquals(result, albumService.getAllAlbums(1,1));
    }
    @Test
    @DisplayName("Get all album, when not exist elements")
    void getAllAlbums_whenElementsNotExist() {

        Album album = new Album();
        album.setTitle("Albúm Jesús");
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        Page<Album> albumPage = Page.empty();

        AlbumResponse albumResponse = new AlbumResponse();
        albumResponse.setId(1l);

        AlbumResponse[] albumList = {};

        when(albumRepository.findAll((any(Pageable.class)))).thenReturn(albumPage);
        when(modelMapper.map(any(), any())).thenReturn(albumList);
        assertEquals(0, albumService.getAllAlbums(1,1).getTotalElements());
    }

}