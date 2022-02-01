package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void getCommentId_success() {
        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(post);


        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertEquals(post.getId(), comment.getPost().getId());
        assertEquals(comment, commentService.getComment(1L, 1L));
    }

    @Test
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

        assertNotEquals(post.getId(), comment.getPost().getId());
        assertThrows(BlogapiException.class, () -> commentService.getComment(1L, 1L));

    }

    @Test
    void getCommentId_postIdNotFound_ResourceNotFoundException() {

        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getComment(1L, 1L));
    }

    @Test
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


        Post newPost = new Post();
        newPost.setId(2L);
        newPost.setBody("Esta post tiene un gran significado para mí");
        newPost.setCreatedAt(Instant.now());
        newPost.setUpdatedAt(Instant.now());

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


        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertNotEquals(post.getId(), comment.getPost().getId());

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Comment does not belong to post");

        assertEquals(apiResponse, commentService.deleteComment(1L, 1L, userPrincipal));

    }

   @Test
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


        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

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

        assertEquals(post.getId(), comment.getPost().getId());
        assertNotEquals(comment.getUser().getId(),userPrincipal.getId());

        assertThrows(BlogapiException.class, () -> commentService.deleteComment(1L,1L,userPrincipal));
    }

    @Test
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
    @Test
    void getAllComments_success(){

        Post post = new Post();
        post.setTitle("Post buenardo");
        post.setId(1L);

        Comment comment = new Comment();
        comment.setName("Pepe");
        comment.setPost(post);

        Page<Comment> resultado = new PageImpl<>(Arrays.asList(comment));

        PagedResponse<Comment> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(resultado.getContent());
        pagedResponse.setTotalPages(1);
        pagedResponse.setTotalElements(1);
        pagedResponse.setLast(true);
        pagedResponse.setSize(1);

        Pageable pageable = PageRequest.of(1, 10);

        when(commentRepository.findByPostId(any(Long.class), any(Pageable.class))).thenReturn(resultado);

        assertEquals(pagedResponse, commentService.getAllComments(1L, 1, 10));


    }



}