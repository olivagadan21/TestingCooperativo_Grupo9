package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PhotoRepositoryTest {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void findByAlbumId_success() {

        Album album = new Album();
        album.setTitle("Viaje de fin de curso");
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        testEntityManager.persist(album);

        Photo photo = new Photo();
        photo.setTitle("Torre Eiffel");
        photo.setUrl("https://media.tacdn.com/media/attractions-splice-spp-674x446/06/74/ab/3e.jpg");
        photo.setThumbnailUrl("https://www.toureiffel.paris/sites/default/files/styles/1200x675/public/actualite/image_principale/IMG_20200526_123909.jpg?itok=DeDSW4xL");
        photo.setCreatedAt(Instant.now());
        photo.setUpdatedAt(Instant.now());
        photo.setAlbum(album);

        testEntityManager.persist(photo);

        Pageable pageable = PageRequest.of(0,1);

        Page<Photo> photoPage = new PageImpl<>(List.of(photo));
        Page<Photo> photos = photoRepository.findByAlbumId(album.getId(), pageable);

        assertEquals(photoPage.getContent(), photos.getContent());

    }



    @Test
    public void findByAlbumIdNonExisting_success() {

        Pageable pageable = PageRequest.of(0,1);

        Page<Photo> photos = photoRepository.findByAlbumId(1L, pageable);

        assertEquals(0, photos.getTotalElements());
    }

}