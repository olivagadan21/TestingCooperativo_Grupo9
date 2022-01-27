package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.PhotoRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PhotoServiceImplTest {

    @Mock
    AlbumRepository albumRepository;

    @Mock
    PhotoRepository photoRepository;


    @InjectMocks
    PhotoServiceImpl photoService;

    @Test
    void addPhoto_success() {

        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
        List<Role> listRole = new ArrayList<>();
        listRole.add(role);

        User user = new User();
        user.setId(1L);
        user.setUpdatedAt(Instant.now());
        user.setEmail("jesus@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Jesús");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Album photos");
        album.setUser(user);

        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Solicitud de fotos");
        photoRequest.setUrl("https://photoreques");
        photoRequest.setThumbnailUrl("https://photoRequest");
        photoRequest.setAlbumId(1L);

        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.of(album));

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Photo photo = new Photo(photoRequest.getTitle(), photoRequest.getUrl(), photoRequest.getThumbnailUrl(),
                album);

        when(photoRepository.save(photo)).thenReturn(photo);

        PhotoResponse photoResponse = new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(),
                photo.getThumbnailUrl(), photo.getAlbum().getId());

        assertEquals(album.getUser().getId(), user.getId());
        assertEquals(photoResponse, photoService.addPhoto(photoRequest, userPrincipal));

    }

    @Test
    void addPhoto_UnauthorizedException() {

        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
        List<Role> listRole = new ArrayList<>();
        listRole.add(role);

        User primerUsuario = new User();
        primerUsuario.setId(1L);
        primerUsuario.setUpdatedAt(Instant.now());
        primerUsuario.setEmail("jesus@gmail.com");
        primerUsuario.setPassword("12345678");
        primerUsuario.setFirstName("Jesús");
        primerUsuario.setCreatedAt(Instant.now());
        primerUsuario.setRoles(listRole);

        User segundoUsuario = new User();
        segundoUsuario.setId(2L);
        segundoUsuario.setUpdatedAt(Instant.now());
        segundoUsuario.setEmail("luismi@gmail.com");
        segundoUsuario.setPassword("12345678");
        segundoUsuario.setFirstName("Luis Miguel");
        segundoUsuario.setCreatedAt(Instant.now());
        segundoUsuario.setRoles(listRole);

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Album photos");
        album.setUser(segundoUsuario);

        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Solicitud de fotos");
        photoRequest.setUrl("https://photoreques");
        photoRequest.setThumbnailUrl("https://photoRequest");
        photoRequest.setAlbumId(1L);

        UserPrincipal userPrincipal = UserPrincipal.create(primerUsuario);

        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.of(album));

        assertThrows(UnauthorizedException.class, () -> photoService.addPhoto(photoRequest, userPrincipal));

    }

    @Test
    void addPhoto_when_albumIsEmpty () {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
        List<Role> listRole = new ArrayList<>();
        listRole.add(role);

        User user = new User();
        user.setId(1L);
        user.setUpdatedAt(Instant.now());
        user.setEmail("jesus@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Jesús");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        PhotoRequest photoRequest = new PhotoRequest();

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> photoService.addPhoto(photoRequest, userPrincipal));
    }


}