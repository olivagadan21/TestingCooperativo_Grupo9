package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;


    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void repoNotNull() {
        assertNotNull(commentRepository);
        assertNotNull(postRepository);
    }

    @Test
    public void findByPostIdInComment_success() {
        Post post = new Post();
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        testEntityManager.persist(post);

        Comment comment = new Comment();
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(post);
        testEntityManager.persist(comment);

        Page<Comment> pageComment = new PageImpl<>(Arrays.asList(comment));

        Page<Comment> comments = commentRepository.findByPostId(1L, any(Pageable.class));

        assertEquals(pageComment, comments);
    }

    @Test
    public void findByPostIdNonExistingInComment() {
        Page<Comment> comments = commentRepository.findByPostId(1L, any(Pageable.class));

        assertEquals(0, comments.getTotalElements());
    }


}