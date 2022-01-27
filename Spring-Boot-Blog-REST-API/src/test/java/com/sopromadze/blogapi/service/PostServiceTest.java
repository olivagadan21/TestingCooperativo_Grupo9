package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.*;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.*;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sopromadze.blogapi.utils.AppConstants.ID;
import static com.sopromadze.blogapi.utils.AppConstants.POST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    PostServiceImpl postService;

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
    void deletePost_throwResourceNotFoundException(){

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
    void deletePost_throwUnauthorizedException(){

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
    void addPost_throwResourceNotFoundExceptionForUser(){

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
    



}
