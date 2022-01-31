package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
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
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.*;

import static com.sopromadze.blogapi.utils.AppConstants.CREATED_AT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PostServiceImplTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    PostServiceImpl postService;

    @Test
    void getPostsByCreatedBy_success() {

        //SIN TERMINAR

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
        post.setBody("Esta post tiene un gran significado para mí");
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        Page<Post> postsPage = new PageImpl<>(Arrays.asList(post));

        Page<Post> posts = postRepository.findAll(any(Pageable.class));

        PagedResponse<Post> postPagedResponse = new PagedResponse<>();
        postPagedResponse.setContent(postsPage.getContent());
        postPagedResponse.setTotalElements(1);
        postPagedResponse.setLast(true);
        postPagedResponse.setSize(1);
        postPagedResponse.setTotalPages(1);

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, CREATED_AT);

        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(postRepository.findByCreatedBy(user.getId(), pageable)).thenReturn(posts);

        assertEquals(posts, postService.getPostsByCreatedBy(user.getUsername(), 0, 10));

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
