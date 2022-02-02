package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    CommentServiceImpl commentService;

    /*
     * Test: Se comprueba que el método devuelve Comment
     * Entrada: commentService.getComment(1L, 1L)
     * Salida esperada: Test se realiza con éxito y devuelve el Comment indicado por ID
     */
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

    /*
     * Test: Se comprueba que el método devuelve la excepción indicada BlogapiException
     * Entrada: commentService.getComment(1L, 1L)
     * Salida esperada: Test se realiza con éxito y lanza la excepción BlogapiException
     */
    @Test
    @DisplayName("Get comment id, exception")
    void getCommentId_exception (){
        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        Post secondPost = new Post();
        secondPost.setId(2L);
        secondPost.setBody("Es bastante chula");
        secondPost.setCreatedAt(Instant.now());
        secondPost.setUpdatedAt(Instant.now());

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Comentario sobre mi viaje de fin de curso");
        comment.setBody("Fuimos a Francia y fue una gran experiencia para mi vida");
        comment.setEmail("barco@gmail.com");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        comment.setPost(secondPost);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        assertThrows(BlogapiException.class, () -> commentService.getComment(1L, 1L));

    }
    /*
     * Test: Se comprueba que el método devuelve la excepción indicada ResourceNotFoundException
     * Entrada: commentService.getComment(1L, 1L)
     * Salida esperada: Test se realiza con éxito y lanza la excepción ResourceNotFoundException
     */
    @Test
    @DisplayName("Get comment id, post id not found")
    void getCommentId_postIdNotFound_ResourceNotFoundException() {

        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.getComment(1L, 1L));
    }

    /*
     * Test: Se comprueba que el método devuelve la excepción indicada ResourceNotFoundException
     * Entrada: commentService.getComment(1L, 1L)
     * Salida esperada: Test se realiza con éxito y lanza la excepción ResourceNotFoundException
     */
    @Test
    @DisplayName("Get comment id, comment id not found")
    void getCommentId_commentIdNotFound_ResourceNotFoundException() {

        Post post = new Post();
        post.setId(2L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.getComment(1L, 1L));
    }

    /*
     * Test: Se comprueba que el método devuelve elimina Comment indicado por ID
     * Entrada: commentService.getComment(1L, 1L)
     * Salida esperada: Test se realiza con éxito y elimina el comentario
     */
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

    /*
     * Test: Se comprueba que el método devuelve ApiResponse
     * Entrada: commentService.getComment(1L, 1L)
     * Salida esperada: Test se realiza con éxito y devuelve ApiResponse
     */
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

    /*
     * Test: Se comprueba que el método devuelve la excepción indicada BlogapiException
     * Entrada: commentService.getComment(1L, 1L)
     * Salida esperada: Test se realiza con éxito y lanza la excepción BlogapiException
     */
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

    /*
     * Test: Se comprueba que el método devuelve la excepción indicada ResourceNotFoundException
     * Entrada: commentService.getComment(1L, 1L)
     * Salida esperada: Test se realiza con éxito y lanza la excepción ResourceNotFoundException
     */
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
    /*
     * Test: Se comprueba que el método devuelve la excepción indicada ResourceNotFoundException
     * Entrada: commentService.getComment(1L, 1L)
     * Salida esperada: Test se realiza con éxito y lanza la excepción ResourceNotFoundException
     */
    @Test
    @DisplayName("Delete comment, comment id not found")
    void deleteComment_commentIdNonExists_ResourceNotFoundException(){

        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);
        List<Role> listRole = new ArrayList<>();
        listRole.add(role);

        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        User user = new User();
        user.setId(1L);
        user.setUpdatedAt(Instant.now());
        user.setEmail("jesus@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Jesús");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
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
    void addComment_ResourceNotFoundException_success() {

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
    void updateComment_success() {

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
    void updateComment_ResourceNotFoundException_Post_success() {

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
    void updateComment_ResourceNotFoundException_Comment_success() {

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