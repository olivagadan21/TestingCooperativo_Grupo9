package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PhotoServiceImplTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PhotoRepository photoRepository;

    @InjectMocks
    PhotoServiceImpl photoService;

    @Test
    void getAllPhotos_success() {

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Viaje de fin de curso");

        Photo photo = new Photo();
        photo.setTitle("Torre Eiffel");
        photo.setUrl("https://media.tacdn.com/media/attractions-splice-spp-674x446/06/74/ab/3e.jpg");
        photo.setThumbnailUrl("https://www.toureiffel.paris/sites/default/files/styles/1200x675/public/actualite/image_principale/IMG_20200526_123909.jpg?itok=DeDSW4xL");
        photo.setAlbum(album);

        Page<Photo> photoPage = new PageImpl<>(Arrays.asList(photo));

        PagedResponse<Photo> photoPagedResponse = new PagedResponse<>();
        photoPagedResponse.setContent(photoPage.getContent());
        photoPagedResponse.setTotalElements(1);
        photoPagedResponse.setLast(true);
        photoPagedResponse.setSize(1);
        photoPagedResponse.setTotalPages(1);

        Pageable pageable = PageRequest.of(1, 10);

        when(photoRepository.findByAlbumId(any(Long.class), any(Pageable.class))).thenReturn(photoPage);

        assertEquals(photoPagedResponse, photoService.getAllPhotos(1, 10));

    }

    @Test
    void getAllPhotosWithContentIsEmpty_success() {

        Page<Photo> photoPage = new PageImpl<>(Arrays.asList());

        Page<Photo> photos = photoRepository.findAll(any(Pageable.class));

        PagedResponse<Photo> postPagedResponse = new PagedResponse<>();
        postPagedResponse.setContent(photoPage.getContent());
        postPagedResponse.setLast(true);
        postPagedResponse.setTotalPages(1);

        when(photos).thenReturn(photoPage);

        assertEquals(postPagedResponse, photoService.getAllPhotos(0, 10));


    }

    @Test
    void deletePhoto_success() {

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
        albumRepository.save(album);

        Photo photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Torre Eiffel");
        photo.setUrl("https://media.tacdn.com/media/attractions-splice-spp-674x446/06/74/ab/3e.jpg");
        photo.setThumbnailUrl("https://www.toureiffel.paris/sites/default/files/styles/1200x675/public/actualite/image_principale/IMG_20200526_123909.jpg?itok=DeDSW4xL");
        photo.setAlbum(album);
        photoRepository.save(photo);

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        assertEquals(photo.getAlbum().getUser().getId(), userPrincipal.getId());

        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "Photo deleted successfully");

        assertEquals(apiResponse, photoService.deletePhoto(1L, userPrincipal));

    }

    @Test
    void deletePhotoWhenPhotoIdDoesNotExist_success() {

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

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Photo does not exist");

        assertEquals(apiResponse, photoService.deletePhoto(1L, userPrincipal));

    }

}
