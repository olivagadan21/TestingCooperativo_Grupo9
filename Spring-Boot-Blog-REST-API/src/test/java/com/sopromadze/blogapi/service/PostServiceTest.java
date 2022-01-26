package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.repository.PostRepository;
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

        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("aaa", "bbb", 1L);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(false);
        apiResponse.setMessage("You don't have permission to delete this post");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.deleteById(1L)).thenReturn();
        assertThrows(resourceNotFoundException.getClass(), ()->postService.deletePost(2L, userPrincipal));
        //assertThrows(new UnauthorizedException(apiResponse).getClass(), ()->postService.deletePost(1L, userPrincipal));
    }
    @Test
    void deletePost_throwUnauthorizedException(){

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol);

        Role rol2 = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles2 = Arrays.asList(rol2);

        User user = new User();
        user.setId(2L);
        user.setRoles(roles);

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Post buenardo");

        User user2 = new User();
        user2.setId(4L);
        user2.setRoles(roles2);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(false);
        apiResponse.setMessage("You don't have permission to delete this post");

        lenient().when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        System.out.println(postRepository.findById(1L));
        //assertThrows(resourceNotFoundException.getClass(), ()->postService.deletePost(2L, userPrincipal));
        assertThrows(new UnauthorizedException(apiResponse).getClass(), ()->postService.deletePost(1L, userPrincipal));
    }


}
