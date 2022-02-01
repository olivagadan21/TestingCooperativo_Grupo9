package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestConfig;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestConfig.class})
class CommentControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    @Test
    @DisplayName("Get comment return 200")
    void getComment_success() throws Exception{
        Post post = new Post();
        post.setId(1L);
        post.setBody("Mi post favorita");
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setId(1L);
        comment.setEmail("barco@gmail.com");

        when(commentService.getComment(1L,1L)).thenReturn(comment);

        mockMvc.perform(get("/api/posts/{postId}/comments/{id}",1L,1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Delete comment return 200")
    @WithUserDetails("ADMIN")
    void deleteComment_success() throws Exception{
        ApiResponse apiResponse = new ApiResponse(true, "You successfully deleted comment");

        Post post = new Post();
        post.setId(1L);
        post.setBody("Mi post favorita");
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setId(1L);
        comment.setEmail("barco@gmail.com");

        when(commentService.deleteComment(anyLong(), anyLong(), any())).thenReturn(apiResponse);
        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", 1L, 1L)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Delete comment return 400")
    @WithUserDetails("ADMIN")
    void deleteComment_badRequest() throws Exception{
        ApiResponse apiResponse = new ApiResponse(false, "You successfully deleted comment");

        Post post = new Post();
        post.setId(1L);
        post.setBody("Mi post favorita");
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setId(1L);
        comment.setEmail("barco@gmail.com");

        when(commentService.deleteComment(anyLong(), anyLong(), any())).thenReturn(apiResponse);
        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", 1L, 1L)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Delete comment return 401")
    void deleteComment_Unauthorized() throws Exception{
        ApiResponse apiResponse = new ApiResponse(false, "You successfully deleted comment");

        Post post = new Post();
        post.setId(1L);
        post.setBody("Mi post favorita");
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setId(1L);
        comment.setEmail("barco@gmail.com");

        when(commentService.deleteComment(anyLong(), anyLong(), any())).thenReturn(apiResponse);
        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", 1L, 1L)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }
}