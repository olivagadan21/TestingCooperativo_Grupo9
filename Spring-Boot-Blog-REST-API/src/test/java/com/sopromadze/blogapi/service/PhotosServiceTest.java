package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
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
import com.sopromadze.blogapi.service.impl.PhotoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PhotosServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    PhotoServiceImpl photoService;

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
    void getPhoto_throwResourceNotFoundException(){
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




}
