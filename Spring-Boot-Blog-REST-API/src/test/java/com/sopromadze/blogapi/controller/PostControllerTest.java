package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
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
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.PostServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostServiceImpl postServiceImpl;


    private UserPrincipal userPrincipal;
    private UserPrincipal userPrincipal2;
    private User user;
    private User user2;
    private Role rol;
    private Role rol2;
    private ApiResponse apiResponse;
    private Post post1;
    private PostRequest postRequest;
    private Post post2;
    private Category category;
    private Tag tag;
    private PostResponse postResponse;
    private PagedResponse<Post> pagedResponse;

    @BeforeEach
    void initData() {

        rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        rol2 = new Role();
        rol2.setName(RoleName.ROLE_USER);

        List<Role> roles2 = Arrays.asList(rol2);

        post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Post buenardo");
        post1.setCreatedAt(Instant.now());
        post1.setUpdatedAt(Instant.now());

        user = new User();
        user.setId(3L);
        user.setRoles(roles);
        user.setPosts(List.of(post1));

        user2 = new User();
        user2.setId(4L);
        user2.setRoles(roles2);
        user2.setPosts(List.of(post1));

        userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        userPrincipal2 = UserPrincipal.builder()
                .id(user2.getId())
                .authorities(user2.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();


        apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("You successfully deleted post");


        category = new Category();
        category.setId(2L);
        category.setCreatedBy(user.getId());
        category.setName("Categoría chula");

        tag = new Tag();
        tag.setName("Tag bueno");


        List<String> tags = Arrays.asList(tag.getName());

        List<Tag> tags2 = Arrays.asList(tag);

        postRequest = new PostRequest();
        postRequest.setCategoryId(category.getId());
        postRequest.setTitle("Post Buenardo");
        postRequest.setBody("Post muy bueno, tan bueno que lo pongo aqui porque me gusta mucho");
        postRequest.setTags(tags);

        post2 = new Post();
        post2.setBody(postRequest.getBody());
        post2.setTitle(postRequest.getTitle());
        post2.setBody(postRequest.getBody());
        post2.setCategory(category);
        post2.setUser(user);
        post2.setTags(tags2);

        postResponse = new PostResponse();

        postResponse.setTitle(post2.getTitle());
        postResponse.setBody(post2.getBody());
        postResponse.setCategory(post2.getCategory().getName());
        postResponse.setTags(tags);

        Page<Post> posts = new PageImpl<>(List.of(post2));


        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();


        pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(content);
        pagedResponse.setTotalPages(1);
        pagedResponse.setSize(1);
        pagedResponse.setTotalElements(1);
        pagedResponse.setLast(true);



    }

    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @Test
    void whenDeletePost_returns200() throws Exception {

        System.out.println(postServiceImpl);

        when(postServiceImpl.deletePost(post1.getId(), userPrincipal)).thenReturn(apiResponse);
        mockMvc.perform(delete("/api/posts/{id}", post1.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());

    }


    @Test
    void whenDeletePost_returns401() throws Exception {

        System.out.println(postServiceImpl);

        when(postServiceImpl.deletePost(post1.getId(), userPrincipal)).thenReturn(apiResponse);
        mockMvc.perform(delete("/api/posts/{id}", post1.getId())
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());

    }

    @WithMockUser(authorities = {"ROLE_USER"})
    @Test
    void whenAddPost_returns200() throws Exception {

        when(postServiceImpl.addPost(postRequest, userPrincipal)).thenReturn(postResponse);

        mockMvc.perform(post("/api/posts")
                .content(objectMapper.writeValueAsString(postRequest))
                .contentType("application/json"))
                .andExpect(status().isCreated()).andDo(print());

    }


    @Test
    void whenAddPost_returns401() throws Exception{

        when(postServiceImpl.addPost(postRequest, userPrincipal)).thenReturn(postResponse);

        mockMvc.perform(post("/api/posts")
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    void whenGetPostsByCategory_returns200() throws Exception {

        when(postServiceImpl.getPostsByCategory(category.getId(), 1, 10)).thenReturn(pagedResponse);
        mockMvc.perform(get("/api/posts/category/{id}", 2L)
                        .param("page", String.valueOf(1))
                        .param("size", String.valueOf(10))
                        .contentType("application/json"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(pagedResponse)))
                .andExpect(status().isOk()).andDo(print());

    }

    @Test
    @DisplayName("Get all posts return 200")
    void getAllPosts_success() throws Exception {
        Post post = new Post();
        post.setBody("Esta post es mi preferida");

        List<Post> list = new ArrayList<>();
        list.add(post);

        Page<Post> postsPage = new PageImpl<>(Arrays.asList(post));

        PagedResponse<Post> postPagedResponse = new PagedResponse<>();
        postPagedResponse.setContent(postsPage.getContent());
        postPagedResponse.setTotalElements(1);
        postPagedResponse.setLast(true);
        postPagedResponse.setSize(1);
        postPagedResponse.setTotalPages(1);

        when(postServiceImpl.getAllPosts(1,1)).thenReturn(postPagedResponse);

        mockMvc.perform(get("/api/posts")
                        .param("size","1").param("page","1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(postPagedResponse)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update post return 200")
    @WithUserDetails("ADMIN")
    void updatePost_success() throws Exception {
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Post editada por Jesús Barco");
        postRequest.setCategoryId(1L);
        postRequest.setBody("Este tipo de imagen tiene gran exíto en Facebook e Instagram, sobre todo porque " +
                "hoy en día vivimos muy aceleradamente, estamos todos los días buscando motivación e inspiración");

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update post return 400, \n" +
            "because it has no category id")
    @WithUserDetails("ADMIN")
    void updatePost_badRequest() throws Exception {

        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Post editada por Jesús Barco");
        postRequest.setBody("Este tipo de imagen tiene gran exíto en Facebook e Instagram, sobre todo porque " +
                "hoy en día vivimos muy aceleradamente, estamos todos los días buscando motivación e inspiración");

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update post return 401, unauthorized")
    void updatePost_Unauthorized() throws Exception {

        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Post editada por Jesús Barco");
        postRequest.setCategoryId(1L);
        postRequest.setBody("Este tipo de imagen tiene gran exíto en Facebook e Instagram, sobre todo porque " +
                "hoy en día vivimos muy aceleradamente, estamos todos los días buscando motivación e inspiración");

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get post by tag return 200")
    void getPostsByTag_success() throws Exception{
        Post post = new Post();
        post.setBody("Esta post es mi preferida");

        List<Post> list = new ArrayList<>();
        list.add(post);

        Page<Post> postsPage = new PageImpl<>(Arrays.asList(post));

        PagedResponse<Post> postPagedResponse = new PagedResponse<>();
        postPagedResponse.setContent(postsPage.getContent());
        postPagedResponse.setTotalElements(1);
        postPagedResponse.setLast(true);
        postPagedResponse.setSize(1);
        postPagedResponse.setTotalPages(1);

        when(postServiceImpl.getPostsByTag(1L,1,1)).thenReturn(postPagedResponse);
        mockMvc.perform(get("/api/posts/tag/{id}",1L)
                        .param("size","1").param("page","1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(postPagedResponse)))
                .andExpect(status().isOk());
    }

}
