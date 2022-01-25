package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceImplTest {

    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    void test_findByCommentId() {
        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        postRepository.save(post);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(post);
        commentRepository.save(comment);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertEquals(post.getId(), comment.getPost().getId());
        assertEquals(comment, commentService.getComment(1L, 1L));
    }

    @Test
    void test_deleteComment() {

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

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        postRepository.save(post);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.save(comment);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertEquals(post.getId(), comment.getPost().getId());

        assertEquals(comment.getUser().getId(), userPrincipal.getId());

        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "You successfully deleted comment");

        assertEquals(apiResponse, commentService.deleteComment(1L, 1L, userPrincipal));

    }

    @Test
    void test_deleteComment_when_postId_notEquals_commentPostId() {

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

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        postRepository.save(post);

        Post newPost = new Post();
        newPost.setId(2L);
        newPost.setBody("Esta post tiene un gran significado para mí");
        newPost.setCreatedAt(Instant.now());
        newPost.setUpdatedAt(Instant.now());
        postRepository.save(newPost);
        
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(newPost);
        comment.setUser(user);
        commentRepository.save(comment);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertNotEquals(post.getId(), comment.getPost().getId());

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Comment does not belong to post");

        assertEquals(apiResponse, commentService.deleteComment(1L, 1L, userPrincipal));

    }

   /* @Test
    void test_findByCommentId_exception (){
        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        postRepository.save(post);

        Post post1 = new Post();
        post1.setId(2L);
        post1.setBody("Esta post tiene un gran significado para mí");
        post1.setCreatedAt(Instant.now());
        post1.setUpdatedAt(Instant.now());
        postRepository.save(post1);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(post1);
        commentRepository.save(comment);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertNotEquals(post.getId(), comment.getPost().getId());
        BlogapiException blogapiException = new BlogapiException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        assertEquals(blogapiException,commentService.getComment(1L,1L));
    }*/

}