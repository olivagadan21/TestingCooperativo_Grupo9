package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void addComment_access() {

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(roleList);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Post post = new Post();
        post.setId(1L);
        post.setUser(user);
        post.setTitle("Viaje a Francia");

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("Esto es el body");

        Comment comment = new Comment();
        comment.setName("Hola");
        comment.setBody(commentRequest.getBody());
        comment.setPost(post);

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        assertEquals(comment, commentService.addComment(commentRequest,post.getId(),userPrincipal));

    }

    @Test
    void addComment_ResourceNotFoundException_access() {

        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(roleList);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Post post = new Post();
        post.setId(1L);
        post.setUser(user);
        post.setTitle("Viaje a Francia");

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("Esto es el body");

        Comment comment = new Comment();
        comment.setName("Hola");
        comment.setBody(commentRequest.getBody());
        comment.setPost(post);

        when(postRepository.findById(2L)).thenReturn(Optional.of(post));

        assertThrows(ResourceNotFoundException.class, ()->commentService.addComment(commentRequest, post.getId(), userPrincipal));

    }

    @Test
    void updateComment_access() {

        Role rol = new Role();
        rol.setId(1L);
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(roleList);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Post post = new Post();
        post.setId(1L);
        post.setUser(user);
        post.setTitle("Viaje a Francia");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Hola");
        comment.setBody("Esto es el body");
        comment.setPost(post);
        comment.setUser(user);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("Body");

        Comment updated = new Comment();
        updated.setId(comment.getId());
        updated.setBody(commentRequest.getBody());
        updated.setName(comment.getName());
        updated.setPost(comment.getPost());


        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(updated);

        assertEquals(true,userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        assertNotEquals(comment, commentService.updateComment(post.getId(), comment.getId(), commentRequest, userPrincipal));

    }

    @Test
    void updateComment_ResourceNotFoundException_Post_access() {

        Role rol = new Role();
        rol.setId(1L);
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(roleList);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Post post = new Post();
        post.setId(1L);
        post.setUser(user);
        post.setTitle("Viaje a Francia");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Hola");
        comment.setBody("Esto es el body");
        comment.setPost(post);
        comment.setUser(user);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("Body");

        Comment updated = new Comment();
        updated.setId(comment.getId());
        updated.setBody(commentRequest.getBody());
        updated.setName(comment.getName());
        updated.setPost(comment.getPost());


        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        assertThrows(ResourceNotFoundException.class, ()-> commentService.updateComment(post.getId(), comment.getId(), commentRequest, userPrincipal));

    }

    @Test
    void updateComment_ResourceNotFoundException_Comment_access() {

        Role rol = new Role();
        rol.setId(1L);
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(roleList);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Post post = new Post();
        post.setId(1L);
        post.setUser(user);
        post.setTitle("Viaje a Francia");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Hola");
        comment.setBody("Esto es el body");
        comment.setPost(post);
        comment.setUser(user);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("Body");

        Comment updated = new Comment();
        updated.setId(comment.getId());
        updated.setBody(commentRequest.getBody());
        updated.setName(comment.getName());
        updated.setPost(comment.getPost());


        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));
        assertThrows(ResourceNotFoundException.class, ()-> commentService.updateComment(post.getId(), comment.getId(), commentRequest, userPrincipal));

    }

}
