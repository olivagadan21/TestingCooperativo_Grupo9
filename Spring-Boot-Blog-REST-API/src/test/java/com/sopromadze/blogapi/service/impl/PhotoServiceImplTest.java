package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
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
import java.util.stream.Collectors;

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
    void test_addPhoto() {

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
        albumRepository.save(album);

        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Solicitud de fotos");
        photoRequest.setUrl("https://photoreques");
        photoRequest.setThumbnailUrl("https://photoRequest");
        photoRequest.setAlbumId(1L);

        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.of(album));

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Photo photo = new Photo(photoRequest.getTitle(), photoRequest.getUrl(), photoRequest.getThumbnailUrl(),
                album);
        photoRepository.save(photo);

        when(photoRepository.save(photo)).thenReturn(photo);


        PhotoResponse photoResponse = new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(),
                photo.getThumbnailUrl(), photo.getAlbum().getId());

        assertEquals(album.getUser().getId(), user.getId());
        assertEquals(photoResponse, photoService.addPhoto(photoRequest, userPrincipal));

    }

   /* @Test
    void test_addPhoto_when_albumUserId_isNotEquals_userId() {

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
        segundoUsuario.setEmail("jesus@gmail.com");
        segundoUsuario.setPassword("12345678");
        segundoUsuario.setFirstName("Jesús");
        segundoUsuario.setCreatedAt(Instant.now());
        segundoUsuario.setRoles(listRole);

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Album photos");
        album.setUser(segundoUsuario);
        albumRepository.save(album);

        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Solicitud de fotos");
        photoRequest.setUrl("https://photoreques");
        photoRequest.setThumbnailUrl("https://photoRequest");
        photoRequest.setAlbumId(1L);

        UserPrincipal userPrincipal = UserPrincipal.create(primerUsuario);
        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.of(album));

        Photo photo = new Photo(photoRequest.getTitle(), photoRequest.getUrl(), photoRequest.getThumbnailUrl(),
                album);
        photoRepository.save(photo);

        when(photoRepository.save(photo)).thenReturn(photo);

        assertNotEquals(album.getUser().getId(), primerUsuario.getId());

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to add photo in this album");

        UnauthorizedException unauthorizedException = new UnauthorizedException(apiResponse);

        assertEquals(unauthorizedException, photoService.addPhoto(photoRequest, userPrincipal));

    }
*/

}