package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void repoNotNull() {
        assertNotNull(tagRepository);
    }

    @Test
    void findByName_success() {
        Tag tag = new Tag();
        tag.setName("#VERANO");
        tag.setCreatedAt(Instant.now());
        tag.setUpdatedAt(Instant.now());

        testEntityManager.persist(tag);

        assertEquals(tag, tagRepository.findByName("#VERANO"));
    }

    @Test
    void findByName_nonExisting() {
        assertEquals(null, tagRepository.findByName("#VERANO"));
    }
}