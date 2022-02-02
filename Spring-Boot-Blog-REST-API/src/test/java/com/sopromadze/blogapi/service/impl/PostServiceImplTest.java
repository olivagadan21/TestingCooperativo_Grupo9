package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.payload.PostResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.TagRepository;
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
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.sopromadze.blogapi.utils.AppConstants.CREATED_AT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

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
    @Test
    void deletePost_success(){
        Role rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();


        Post post = new Post();
        post.setId(1L);
        post.setUser(user);
        post.setTitle("Post buenardo");

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("You successfully deleted post");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        assertEquals(true, post.getUser().getId().equals(userPrincipal.getId()));
        assertEquals(true,userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        doNothing().when(postRepository).deleteById(post.getId());
        assertEquals(apiResponse, postService.deletePost(1L, userPrincipal));

    }

    @Test
    void deletePost_throwsResourceNotFoundException(){

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Post buenardo");

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(3L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(4L)
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("aaa", "bbb", 2L);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(false);
        apiResponse.setMessage("You don't have permission to delete this post");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        doNothing().when(postRepository).deleteById(1L);
        assertThrows(resourceNotFoundException.getClass(), ()->postService.deletePost(2L, userPrincipal));
        //assertThrows(new UnauthorizedException(apiResponse).getClass(), ()->postService.deletePost(1L, userPrincipal));
    }
    @Test
    void deletePost_throwsUnauthorizedException(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);


        User user = new User();
        user.setId(3L);
        user.setRoles(roles);

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Post buenardo");
        post.setUser(user);
        post.setCreatedBy(3L);



        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(4L)
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(false);
        apiResponse.setMessage("You don't have permission to delete this post");

        lenient().when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        System.out.println(postRepository.findById(1L));
        //assertThrows(resourceNotFoundException.getClass(), ()->postService.deletePost(2L, userPrincipal));
        assertThrows(UnauthorizedException.class, ()->postService.deletePost(1L, userPrincipal));
    }

    @Test
    void addPost_success(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);


        User user = new User();
        user.setId(1L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        Category category = new Category();
        category.setId(2L);
        category.setCreatedBy(user.getId());
        category.setName("Categoría chula");

        Tag tag = new Tag();
        tag.setName("Tag bueno");


        List<String> tags = Arrays.asList(tag.getName());

        List<Tag> tags2 = Arrays.asList(tag);

        PostRequest postRequest = new PostRequest();
        postRequest.setCategoryId(category.getId());
        postRequest.setTitle("Post Buenardo");
        postRequest.setTags(tags);

        Post post = new Post();
        post.setBody(postRequest.getBody());
        post.setTitle(postRequest.getTitle());
        post.setCategory(category);
        post.setUser(user);
        post.setTags(tags2);


        PostResponse postResponse = new PostResponse();

        postResponse.setTitle(post.getTitle());
        postResponse.setBody(post.getBody());
        postResponse.setCategory(post.getCategory().getName());
        postResponse.setTags(tags);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(postRequest.getCategoryId())).thenReturn(Optional.of(category));
        when(tagRepository.findByName(tag.getName())).thenReturn(tag);
        when(tagRepository.save(tag)).thenReturn(tag);
        when(postRepository.save(post)).thenReturn(post);
        assertEquals(postResponse, postService.addPost(postRequest, userPrincipal));

    }

    @Test
    void addPost_throwsResourceNotFoundExceptionForUser(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);


        User user = new User();
        user.setId(1L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(2L)
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        Category category = new Category();
        category.setId(2L);
        category.setCreatedBy(user.getId());
        category.setName("Categoría chula");

        Tag tag = new Tag();
        tag.setName("Tag bueno");

        List<String> tags = Arrays.asList(tag.getName());

        PostRequest postRequest = new PostRequest();
        postRequest.setCategoryId(category.getId());
        postRequest.setTitle("Post Buenardo");
        postRequest.setTags(tags);

        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->postService.addPost(postRequest, userPrincipal));
    }

    @Test
    void addPost_throwsResourceNotFoundExceptionForCategory(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);


        User user = new User();
        user.setId(1L);
        user.setRoles(roles);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        Category category = new Category();
        category.setId(2L);
        category.setCreatedBy(user.getId());
        category.setName("Categoría chula");

        Tag tag = new Tag();
        tag.setName("Tag bueno");

        List<String> tags = Arrays.asList(tag.getName());

        PostRequest postRequest = new PostRequest();
        postRequest.setCategoryId(category.getId());
        postRequest.setTitle("Post Buenardo");
        postRequest.setTags(tags);

        when(userRepository.findById(userPrincipal.getId())).thenReturn(Optional.of(user));
        when(categoryRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->postService.addPost(postRequest, userPrincipal));
    }

    @Test
    void getPostByCategory_success(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);


        User user = new User();
        user.setId(1L);
        user.setRoles(roles);


        Category category = new Category();
        category.setId(2L);
        category.setCreatedBy(user.getId());
        category.setName("Categoría chula");
        category.setCreatedAt(Instant.now());


        Post post = new Post();
        post.setId(3L);
        post.setUser(user);
        post.setTitle("Post buenardo");
        post.setCategory(category);

        Page<Post> posts = new PageImpl<>(List.of(post));

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, CREATED_AT);

        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        PagedResponse<Post> result = new PagedResponse<>();
        result.setContent(content);
        result.setTotalPages(1);
        result.setSize(1);
        result.setTotalElements(1);
        result.setLast(true);


        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(postRepository.findByCategory(category.getId(), pageable)).thenReturn(posts);
        assertEquals(result, postService.getPostsByCategory(category.getId(), 1, 1));

    }

    @Test
    void getPostByCategory_throwsResourceNotFoundExceptionForCategory(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setRoles(roles);

        Category category = new Category();
        category.setId(2L);
        category.setCreatedBy(user.getId());
        category.setName("Categoría chula");
        category.setCreatedAt(Instant.now());


        when(categoryRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->postService.getPostsByCategory(3L, 1, 1));

    }

    @Test
    void getPost_success() {

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

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertEquals(post, postService.getPost(post.getId()));

    }

    @Test
    void getPost_ResourceNotFoundException_success() {

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

        when(postRepository.findById(2L)).thenReturn(Optional.of(post));

        assertThrows(ResourceNotFoundException.class, ()-> postService.getPost(post.getId()));

    }

}