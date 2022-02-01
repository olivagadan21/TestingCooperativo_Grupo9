package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.PhotoRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
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

    /*
     * Test: Se comprueba que el método devuelve PhotoResponse
     * Entrada: photoService.addPhoto(photoRequest, userPrincipal)
     * Salida esperada: Test se realiza con éxito y se añade nueva foto
     */
    @Test
    @DisplayName("Add photo")
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

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Photo photo = new Photo(photoRequest.getTitle(), photoRequest.getUrl(), photoRequest.getThumbnailUrl(),
                album);

        PhotoResponse photoResponse = new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(),
                photo.getThumbnailUrl(), photo.getAlbum().getId());

        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.of(album));
        when(photoRepository.save(photo)).thenReturn(photo);
        assertEquals(album.getUser().getId(), user.getId());
        assertEquals(photoResponse, photoService.addPhoto(photoRequest, userPrincipal));

    }
    /*
     * Test: Se comprueba que el método lanza la excepción UnauthorizedException
     * Entrada: photoService.addPhoto(photoRequest, userPrincipal)
     * Salida esperada: Test se realiza con éxito y se lanza la excepción UnauthorizedException
     */
    @Test
    @DisplayName("Add photo unauthorized exception ")
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
    /*
     * Test: Se comprueba que el método lanza la excepción ResourceNotFoundException
     * Entrada: photoService.addPhoto(photoRequest, userPrincipal)
     * Salida esperada: Test se realiza con éxito y se lanza la excepción ResourceNotFoundException
     */
    @Test
    @DisplayName("Add photo, album is empty")
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

    /*
     * Test: Se comprueba que el método devuelve todas las fotos de un albúm
     * Entrada: photoService.getAllPhotosByAlbum(1L,1,1)
     * Salida esperada: Test se realiza con éxito devuelve PagedResponse de fotos
     */
    @Test
    @DisplayName("Get all photos by album")
    void getAllPhotosByAlbum_success() {

        Album album = new Album();
        album.setId(1L);

        Photo photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Mi foto en la playa");
        photo.setAlbum(album);

        Page<Photo> photoPage = new PageImpl<>(Arrays.asList(photo));

        PhotoResponse photoResponse = new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(),
                photo.getThumbnailUrl(), photo.getAlbum().getId());

        List<PhotoResponse> photoResponses = new ArrayList<>();
        photoResponses.add(photoResponse);

        PagedResponse photoPagedResponse =new PagedResponse<>(photoResponses, photoPage.getNumber(), photoPage.getSize(), photoPage.getTotalElements(),
                photoPage.getTotalPages(), photoPage.isLast());

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, AppConstants.CREATED_AT);

        when(photoRepository.findByAlbumId(1L, pageable)).thenReturn(photoPage);
        assertEquals(photoPagedResponse,photoService.getAllPhotosByAlbum(1L,1,1));
    }
    /*
     * Test: Se comprueba que el método devuelve que los elementos son igual a 0
     * Entrada: photoService.getAllPhotosByAlbum(1L,1,1)
     * Salida esperada: Test se realiza con éxito devuelve 0 elementos
     */
    @Test
    @DisplayName("Get all photos by album empty")
    void getAllPhotosByAlbum_whenAlbumIsEmpty() {

        Album album = new Album();
        album.setId(1L);

        Photo photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Mi foto en la playa");
        photo.setAlbum(album);

        Page<Photo> photoPage = new PageImpl<>(Arrays.asList());

        PhotoResponse photoResponse = new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(),
                photo.getThumbnailUrl(), photo.getAlbum().getId());

        List<PhotoResponse> photoResponses = new ArrayList<>();
        photoResponses.add(photoResponse);

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, AppConstants.CREATED_AT);

        when(photoRepository.findByAlbumId(1L, pageable)).thenReturn(photoPage);
        assertEquals(0,photoService.getAllPhotosByAlbum(1L,1,1).getTotalElements());
    }

    @Test
    void getPhoto_success(){

        Album album = new Album();
        album.setTitle("Álbum Reshulon");
        album.setId(2L);
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        Photo photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Foto chula");
        photo.setUrl("https://fotochula");
        photo.setThumbnailUrl("https://fotomuychula");
        photo.setAlbum(album);

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        PhotoResponse photoResponse = new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(), photo.getThumbnailUrl(), photo.getAlbum().getId());

        assertEquals(photoService.getPhoto(1L), photoResponse);
    }

    @Test
    void getPhoto_throwsResourceNotFoundException(){
        Album album = new Album();
        album.setTitle("Álbum Reshulon");
        album.setId(2L);
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        Photo photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Foto chula");
        photo.setUrl("https://fotochula");
        photo.setThumbnailUrl("https://fotomuychula");
        photo.setAlbum(album);

        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("aaa", "bbb", 3L);
        when(photoRepository.findById(photo.getId())).thenReturn(Optional.of(photo));
        assertThrows(resourceNotFoundException.getClass(), ()->photoService.getPhoto(3L));
    }

    @Test
    void updatePhoto_success(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        Album album = new Album();
        album.setTitle("Álbum Reshulon");
        album.setId(2L);
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());
        album.setUser(user);

        Photo photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Foto chula");
        photo.setUrl("https://fotochula");
        photo.setThumbnailUrl("https://fotomuychula");
        photo.setAlbum(album);

        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setAlbumId(album.getId());
        photoRequest.setTitle("Foto muy chula");
        photoRequest.setUrl("https://fotosuperchula");
        photoRequest.setThumbnailUrl("https://fotosupermegachula");

        PhotoResponse photoResponse = new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(), photo.getThumbnailUrl(), photo.getAlbum().getId());

        Photo updated = new Photo();
        updated.setId(photo.getId());
        updated.setAlbum(album);
        updated.setTitle(photoRequest.getTitle());
        updated.setThumbnailUrl(photoRequest.getThumbnailUrl());

        when(albumRepository.findById(photoRequest.getAlbumId())).thenReturn(Optional.of(album));
        when(photoRepository.findById(photo.getId())).thenReturn(Optional.of(photo));
        when(photoRepository.save(photo)).thenReturn(updated);
        assertNotEquals(photoResponse, photoService.updatePhoto(photo.getId(), photoRequest, userPrincipal));

    }

    @Test
    void updatePhoto_throwsResourceNotFoundExceptionForAlbum(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(2L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        Album album = new Album();
        album.setTitle("Álbum Reshulon");
        album.setId(1L);
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        Photo photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Foto chula");
        photo.setUrl("https://fotochula");
        photo.setThumbnailUrl("https://fotomuychula");
        photo.setAlbum(album);

        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setAlbumId(2L);
        photoRequest.setTitle("Foto muy chula");
        photoRequest.setUrl("https://fotosuperchula");
        photoRequest.setThumbnailUrl("https://fotosupermegachula");

        when(albumRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->photoService.updatePhoto(photo.getId(), photoRequest, userPrincipal));
    }
    @Test
    void updatePhoto_throwsResourceNotFoundExceptionForPhoto(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(2L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        Album album = new Album();
        album.setTitle("Álbum Reshulon");
        album.setId(1L);
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        Photo photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Foto chula");
        photo.setUrl("https://fotochula");
        photo.setThumbnailUrl("https://fotomuychula");
        photo.setAlbum(album);

        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setAlbumId(2L);
        photoRequest.setTitle("Foto muy chula");
        photoRequest.setUrl("https://fotosuperchula");
        photoRequest.setThumbnailUrl("https://fotosupermegachula");

        when(photoRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->photoService.updatePhoto(3L, photoRequest, userPrincipal));
    }


}