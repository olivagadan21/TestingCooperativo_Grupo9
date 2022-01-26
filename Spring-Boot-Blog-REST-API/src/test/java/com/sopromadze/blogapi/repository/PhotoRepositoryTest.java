package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import org.springframework.data.domain.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PhotoRepositoryTest {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    AlbumRepository albumRepository;

    @Test
    public void test_findByAlbumIdInPhoto() {

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Viaje de fin de curso");
        albumRepository.save(album);

        Photo photo = new Photo();
        photo.setTitle("Torre Eiffel");
        photo.setUrl("https://media.tacdn.com/media/attractions-splice-spp-674x446/06/74/ab/3e.jpg");
        photo.setThumbnailUrl("https://www.toureiffel.paris/sites/default/files/styles/1200x675/public/actualite/image_principale/IMG_20200526_123909.jpg?itok=DeDSW4xL");
        photoRepository.save(photo);

        Page<Photo> photoPage = new PageImpl<>(Arrays.asList(photo));
        Page<Photo> photos = photoRepository.findByAlbumId(1L, any(Pageable.class));

        assertEquals(photoPage, photos);

    }

    @Test
    public void test__findByAlbumIdNonExistingInComment() {

        Page<Photo> photos = photoRepository.findByAlbumId(1L, any(Pageable.class));

        assertEquals(0, photos.getTotalElements());
    }

}