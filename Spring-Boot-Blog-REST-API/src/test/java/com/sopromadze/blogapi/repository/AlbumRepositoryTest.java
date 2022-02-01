package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.repository.AlbumRepository;

import com.sopromadze.blogapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlbumRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @MockBean
    AlbumRepository albumRepository;

    @Test
    void testNotNull() {
        assertNotNull(albumRepository);
    }

    @Test
    void findByCreatedBy_success() {

        User user = new User();
        user.setUsername("Vicente");
        user.setFirstName("Rufo");
        user.setLastName("Bruh");
        user.setCreatedAt(Instant.now());

        Album album = new Album();
        album.setTitle("Álbum Reshulon");
        album.setUser(user);
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        testEntityManager.persist(album);

        Page<Album> albums = new PageImpl<>(Arrays.asList(album));

        PageRequest pageRequest = PageRequest.of(1,10);

        when(albumRepository.findByCreatedBy(user.getId(), pageRequest )).thenReturn(albums);

        assertNotEquals(0, albumRepository.findByCreatedBy(user.getId(), pageRequest).getTotalElements());
    }

}
