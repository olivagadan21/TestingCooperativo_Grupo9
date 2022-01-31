package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestConfig;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT, classes =
        {SpringSecurityTestConfig.class})
@AutoConfigureMockMvc
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    PostService postService;

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

        when(postService.getAllPosts(1,1)).thenReturn(postPagedResponse);

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

        when(postService.getPostsByTag(1L,1,1)).thenReturn(postPagedResponse);
        mockMvc.perform(get("/api/posts/tag/{id}",1L)
                        .param("size","1").param("page","1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(postPagedResponse)))
                .andExpect(status().isOk());
    }
}