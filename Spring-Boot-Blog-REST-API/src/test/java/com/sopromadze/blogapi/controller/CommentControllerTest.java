package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.CommentServiceImpl;
import com.sopromadze.blogapi.service.impl.CommentServiceImplTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT, classes =
        {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentServiceImpl commentService;

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