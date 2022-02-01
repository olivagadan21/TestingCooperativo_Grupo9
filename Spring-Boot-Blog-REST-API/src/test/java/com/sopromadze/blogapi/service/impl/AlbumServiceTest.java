package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AlbumServiceTest {

    @Mock
    AlbumRepository albumRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    AlbumServiceImpl albumService;

    private Album album;

    private User user;
    private Role rol;
    private Pageable pageable;
    private Page<Album> albums;
    private PagedResponse<Album> result;

    @BeforeEach
    void initData(){




        rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);


        user = new User();
        user.setId(1L);
        user.setUsername("PepeWTF");
        user.setRoles(roles);

        album = new Album();



        albums = new PageImpl<>(List.of(album));

        List<Album> content = albums.getNumberOfElements() > 0 ? albums.getContent() : Collections.emptyList();

        pageable = PageRequest.of(1, 1, Sort.Direction.DESC, "createdAt");

        result = new PagedResponse<>();
        result.setContent(content);
        result.setTotalPages(1);
        result.setSize(1);
        result.setTotalElements(1);
        result.setLast(true);

    }

    @Test
    void getAlbum_success(){
        ResponseEntity<Album> responseEntity = new ResponseEntity<>(album, HttpStatus.OK);
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        assertEquals(albumService.getAlbum(1L), responseEntity);
    }
    @Test
    void getAlbum_throwsResourceNotFoundExceptionForAlbum(){

        when(albumRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()-> albumService.getAlbum(2L));

    }

    @Test
    void getUserAlbums_success(){

        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(albumRepository.findByCreatedBy(user.getId(), pageable)).thenReturn(albums);
        assertEquals(result, albumService.getUserAlbums(user.getUsername(), 1, 1));
    }

    /*
     * Test: Se comprueba que el método devuelve PagedResponse<AlbumResponse>
     * Entrada: albumService.getAllAlbums(1,1)
     * Salida esperada: Test se realiza con éxito
     */
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

    /*
     * Test: Se comprueba que el método devuelve cero elementos
     * Entrada: albumService.getAllAlbums(1,1)
     * Salida esperada: Test se realiza con éxito y no devuelve ningún elemento
     */
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
