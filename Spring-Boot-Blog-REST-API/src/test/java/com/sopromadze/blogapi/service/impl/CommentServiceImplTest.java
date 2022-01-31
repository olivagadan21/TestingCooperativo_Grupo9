package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Get comment id")
    void getCommentId_success() {
        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(post);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        assertEquals(comment, commentService.getComment(1L, 1L));
    }

    @Test
    @DisplayName("Get comment id, exception")
    void getCommentId_exception (){
        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        Post post1 = new Post();
        post1.setId(2L);
        post1.setBody("Esta post tiene un gran significado para mí");
        post1.setCreatedAt(Instant.now());
        post1.setUpdatedAt(Instant.now());

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(post1);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        assertThrows(BlogapiException.class, () -> commentService.getComment(1L, 1L));

    }

    @Test
    @DisplayName("Get comment id, post id not found")
    void getCommentId_postIdNotFound_ResourceNotFoundException() {

        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.getComment(1L, 1L));
    }

    @Test
    @DisplayName("Get comment id, comment id not found")
    void getCommentId_commentIdNotFound_ResourceNotFoundException() {

        Post post1 = new Post();
        post1.setId(2L);
        post1.setBody("Esta post tiene un gran significado para mí");
        post1.setCreatedAt(Instant.now());
        post1.setUpdatedAt(Instant.now());

        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.getComment(1L, 1L));
    }

    @Test
    @DisplayName("Delete comment")
    void deleteComment_success() {
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

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(post);
        comment.setUser(user);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "You successfully deleted comment");
        assertEquals(apiResponse, commentService.deleteComment(1L, 1L, userPrincipal));

    }

    @Test
    @DisplayName("Delete comment  when post id not equals post id")
    void deleteComment_when_postIdNotEqualsCommentPostId() {

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


        Post newPost = new Post();
        newPost.setId(2L);
        newPost.setBody("Esta post tiene un gran significado para mí");
        newPost.setCreatedAt(Instant.now());
        newPost.setUpdatedAt(Instant.now());

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(newPost);
        comment.setUser(user);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        assertNotEquals(post.getId(), comment.getPost().getId());
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Comment does not belong to post");
        assertEquals(apiResponse, commentService.deleteComment(1L, 1L, userPrincipal));

    }

   @Test
   @DisplayName("Delete comment, exception")
    void deleteComment_BlogApiException() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);
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

        User newUser = new User();
        newUser.setId(2L);
        newUser.setUpdatedAt(Instant.now());
        newUser.setEmail("jesus@gmail.com");
        newUser.setPassword("12345678");
        newUser.setFirstName("Jesús");
        newUser.setCreatedAt(Instant.now());
        newUser.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(post);
        comment.setUser(newUser);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        assertEquals(post.getId(), comment.getPost().getId());
        assertNotEquals(comment.getUser().getId(),userPrincipal.getId());
        assertThrows(BlogapiException.class, () -> commentService.deleteComment(1L,1L,userPrincipal));
    }

    @Test
    @DisplayName("Delete comment, post id not found")
    void deleteComment_postIdNonExists_ResourceNotFoundException(){

        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);
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
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(1L, 1L,userPrincipal));
    }



}