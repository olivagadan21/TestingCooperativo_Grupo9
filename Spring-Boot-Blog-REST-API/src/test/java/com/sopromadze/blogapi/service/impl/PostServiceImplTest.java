package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.TagRepository;
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
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    private PostServiceImpl postService;

    /*
     * Test: Se comprueba que el método devuelve PagedResponse de Post
     * Entrada: postService.getAllPosts(0, 10)
     * Salida esperada: Test se realiza con éxito
    */
    @Test
    @DisplayName("Get all posts")
    void getAllPosts_success() {

        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        Page<Post> postsPage = new PageImpl<>(Arrays.asList(post));

        PagedResponse<Post> postPagedResponse = new PagedResponse<>();
        postPagedResponse.setContent(postsPage.getContent());
        postPagedResponse.setTotalElements(1);
        postPagedResponse.setLast(true);
        postPagedResponse.setSize(1);
        postPagedResponse.setTotalPages(1);

        when(postRepository.findAll(any(Pageable.class))).thenReturn(postsPage);

        assertEquals(postPagedResponse, postService.getAllPosts(0, 10));

    }
    /*
     * Test: Se comprueba que el método devuelve PagedResponse de Post sin ningún contenido
     * Entrada: postService.getAllPosts(0, 10)
     * Salida esperada: Test se realiza con éxito, devuelve PagedResponse vacío
     */
    @Test
    @DisplayName("Get all posts, content empty")
    void getAllPosts_contentIsEmpty() {

        Page<Post> postsPage = new PageImpl<>(Arrays.asList());

        PagedResponse<Post> postPagedResponse = new PagedResponse<>();
        postPagedResponse.setContent(postsPage.getContent());
        postPagedResponse.setLast(true);
        postPagedResponse.setTotalPages(1);

        when(postRepository.findAll(any(Pageable.class))).thenReturn(postsPage);

        assertEquals(postPagedResponse, postService.getAllPosts(0, 10));

    }
    /*
     * Test: Se comprueba que el método devuelve Post editada
     * Entrada: postService.updatePost(1L, postRequest, userPrincipal)
     * Salida esperada: Test se realiza con éxito
    */
    @Test
    @DisplayName("Update post")
    void updatePost_success() {
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
        post.setUser(user);

        Category category = new Category();
        category.setId(1L);
        category.setName("Fotos Invierno");

        PostRequest postRequest = new PostRequest();
        postRequest.setCategoryId(category.getId());
        postRequest.setBody("Esta post ya no tiene tanto significado para mí");
        postRequest.setTitle("Solicitud");

        assertEquals(post.getUser().getId(), user.getId());
        post.setTitle(postRequest.getTitle());
        post.setBody(postRequest.getBody());
        post.setCategory(category);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(categoryRepository.findById(postRequest.getCategoryId())).thenReturn(Optional.of(category));
        when(postRepository.save(post)).thenReturn(post);
        assertEquals(post, postService.updatePost(1L, postRequest, userPrincipal));
    }
    /*
     * Test: Se comprueba que el método lanza la excepción ResourceNotFoundException
     * Entrada: postService.updatePost(1L, postRequest, userPrincipal)
     * Salida esperada: Test se realiza con éxito, se lanza la excepción ResourceNotFoundException
    */
    @Test
    @DisplayName("Update post, not found")
    void updatePost_ResourceNotFoundException() {
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


        PostRequest postRequest = new PostRequest();
        postRequest.setBody("Esta post ya no tiene tanto significado para mí");
        postRequest.setTitle("Solicitud");
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(1L, postRequest, userPrincipal));
    }
    /*
     * Test: Se comprueba que el método lanza la excepción UnauthorizedException
     * Entrada: postService.updatePost(1L, postRequest, userPrincipal)
     * Salida esperada: Test se realiza con éxito, se lanza la excepción UnauthorizedException
    */
    @Test
    @DisplayName("Update post, unauthorized")
    void updatePost_UnauthorizedException() {
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

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setUpdatedAt(Instant.now());
        secondUser.setEmail("luismi@gmail.com");
        secondUser.setPassword("12345678");
        secondUser.setFirstName("Luis Miguel");
        secondUser.setCreatedAt(Instant.now());
        secondUser.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        post.setUser(secondUser);

        Category category = new Category();
        category.setId(1L);
        category.setName("Fotos Invierno");

        PostRequest postRequest = new PostRequest();
        postRequest.setCategoryId(category.getId());
        postRequest.setBody("Esta post ya no tiene tanto significado para mí");
        postRequest.setTitle("Solicitud");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        when(categoryRepository.findById(postRequest.getCategoryId())).thenReturn(Optional.of(category));

        assertThrows(UnauthorizedException.class, () -> postService.updatePost(1L, postRequest, userPrincipal));
    }
    /*
     * Test: Se comprueba que el método devuelve PagedResponse de Post
     * Entrada: postService.getPostsByTag(1L,1,1)
     * Salida esperada: Test se realiza con éxito
    */
    @Test
    @DisplayName("Get posts by tag, content empty")
    void getPostsByTag_success () {

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("#Salesianos");

        List<Tag> listTags = new ArrayList<>();
        listTags.add(tag);

        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        post.setTags(listTags);

        Page<Post> postsPage = new PageImpl<>(Arrays.asList(post));

        PagedResponse<Post> postPagedResponse = new PagedResponse<>();
        postPagedResponse.setContent(postsPage.getContent());
        postPagedResponse.setTotalElements(1);
        postPagedResponse.setLast(true);
        postPagedResponse.setSize(1);
        postPagedResponse.setTotalPages(1);

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(postRepository.findByTagsIn(any(),any(Pageable.class))).thenReturn(postsPage);
        assertEquals(postPagedResponse, postService.getPostsByTag(1L,1,1));
    }
    /*
     * Test: Se comprueba que el método lanza la expeción ResourceNotFoundException
     * Entrada: postService.getPostsByTag(1L,1,1)
     * Salida esperada: Test se realiza con éxito y lanza la excepción ResourceNotFoundException
    */
    @Test
    @DisplayName("Get posts by tag, not found")
    void getPostsByTag_notFound () {

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("#Salesianos");

        List<Tag> listTags = new ArrayList<>();
        listTags.add(tag);

        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        post.setTags(listTags);

        Page<Post> postsPage = new PageImpl<>(Arrays.asList(post));

        PagedResponse<Post> postPagedResponse = new PagedResponse<>();
        postPagedResponse.setContent(postsPage.getContent());
        postPagedResponse.setTotalElements(1);
        postPagedResponse.setLast(true);
        postPagedResponse.setSize(1);
        postPagedResponse.setTotalPages(1);

        when(tagRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()-> postService.getPostsByTag(1L,1,1));
    }

    /*
     * Test: Se comprueba que el método no contiene ningún elemento
     * Entrada: postService.getPostsByTag(1L,1,1)
     * Salida esperada: Test se realiza con éxito y contiene 0 elementos
     */
    @Test
    @DisplayName("Get posts by tag")
    void getPostsByTag_contentIsEmpty () {

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("#Salesianos");

        Post post = new Post();
        post.setId(1L);
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        Page<Post> postsPage = new PageImpl<>(Arrays.asList());

        PagedResponse<Post> postPagedResponse = new PagedResponse<>();
        postPagedResponse.setContent(postsPage.getContent());
        postPagedResponse.setTotalElements(0);
        postPagedResponse.setLast(true);
        postPagedResponse.setTotalPages(1);

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(postRepository.findByTagsIn(any(),any(Pageable.class))).thenReturn(postsPage);
        assertEquals(0, postService.getPostsByTag(1L,1,1).getTotalElements());
    }

}