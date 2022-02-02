package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.CommentServiceImpl;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CommentServiceImpl commentService;

    private Post post1;
    private Page<Comment> resultado;
    private PagedResponse<Comment> pagedResponse;

    @BeforeEach
    void initData(){

        post1 = new Post();
        post1.setTitle("Post buenardo");
        post1.setId(1L);

        Comment comment = new Comment();
        comment.setName("Pepe");
        comment.setPost(post1);

        resultado = new PageImpl<>(Arrays.asList(comment));

        pagedResponse = new PagedResponse();
        pagedResponse.setContent(resultado.getContent());
        pagedResponse.setTotalPages(1);
        pagedResponse.setTotalElements(1);
        pagedResponse.setLast(true);
        pagedResponse.setSize(10);

    }

    @Test
    void whenGetAllComments_returns200() throws Exception {

        when(commentService.getAllComments(post1.getId(), 1, 10)).thenReturn(pagedResponse);
        mockMvc.perform(get("/api/posts/{postId}/comments", post1.getId())
                .param("page", String.valueOf(1))
                .param("size", String.valueOf(10))
                .contentType("application/json"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(pagedResponse)))
                .andExpect(status().isOk()).andDo(print());

    }

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
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("USER")
    @DisplayName("Add comment return 201")
    void addComment_success() throws Exception {

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

        mockMvc.perform(post("/api/posts/1/comments")
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @DisplayName("update comment return 200")
    void updateComment() throws Exception {

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

        Category category = new Category();
        category.setId(1L);
        category.setName("Lorem");

        Post post = new Post();
        post.setId(1L);
        post.setUser(user);
        post.setTitle("Viaje a Francia");
        post.setCategory(category);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setName("Lorem Ipsum");
        comment.setBody("Loren ipsum dolor sit amet, consectetur adipiscing elit. Donec bibendum risus eget diam sodales mollis. Phasellus dictum laoreet orci id ultricies. Vestibulum id nisl elit. Fusce vehicula pellentesque elit vel consectetur. Vestibulum sit amet odio hendrerit, pulvinar dolor ac, ornare nunc. Integer ultricies ante diam, nec efficitur lacus varius nec.");
        comment.setPost(post);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec bibendum risus eget diam sodales mollis. Phasellus dictum laoreet orci id ultricies. Vestibulum id nisl elit. Fusce vehicula pellentesque elit vel consectetur. Vestibulum sit amet odio hendrerit, pulvinar dolor ac, ornare nunc. Integer ultricies ante diam, nec efficitur lacus varius nec.");

        Comment updated = new Comment();
        updated.setId(comment.getId());
        updated.setBody(commentRequest.getBody());
        updated.setName(comment.getName());
        updated.setPost(comment.getPost());

        when(commentService.updateComment(post.getId(), comment.getId(), commentRequest, userPrincipal)).thenReturn(updated);

        mockMvc.perform(put("/api/posts/1/comments/{id}", 1L)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}
