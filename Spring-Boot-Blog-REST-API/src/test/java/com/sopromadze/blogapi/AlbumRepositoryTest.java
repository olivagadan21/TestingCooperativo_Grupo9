package com.sopromadze.blogapi;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.repository.AlbumRepository;

import com.sopromadze.blogapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlbumRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    AlbumRepository albumRepository;

    @Test
    void testNotNull() {
        assertNotNull(albumRepository);
    }

    @Test
    void findByCreatedBy_success() {
        Album album = new Album();
        album.setTitle("Álbum Reshulon");
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        testEntityManager.persist(album);

        List<Album> albums = Arrays.asList(album);

        Pageable pageable = (Pageable) PageRequest.of(1,10);

        User user = new User();
        user.setUsername("Pepe");
        user.setAlbums(albums);
        user.setCreatedAt(Instant.now());


        assertNotEquals(0, albumRepository.findByCreatedBy(user.getId(), pageable).getTotalElements());
    }

}
