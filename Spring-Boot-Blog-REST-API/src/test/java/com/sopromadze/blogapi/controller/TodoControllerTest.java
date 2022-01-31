package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestConfig;
import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.TodoServiceImpl;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT, classes =
        {SpringSecurityTestConfig.class})
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    TodoServiceImpl todoService;

    @Test
    @DisplayName("Complete todo return 200")
    @WithUserDetails("USER")
    void completeTodo_success() throws Exception {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Completado");
        todo.setCompleted(true);
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
        UserPrincipal userPrincipal = UserPrincipal.create(user);

        when(todoService.completeTodo(1L,userPrincipal)).thenReturn(todo);
        mockMvc.perform(put("/api/todos/{id}/complete", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Complete todo unauthorized return 401")
    void completeTodo_unauthorized() throws Exception {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Completado");
        todo.setCompleted(true);

        mockMvc.perform(put("/api/todos/{id}/complete", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Update todo return 200")
    @WithUserDetails("USER")
    void updateTodo_success() throws Exception{
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Completado");
        todo.setCompleted(true);

        mockMvc.perform(put("/api/todos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update todo unauthorized return 401")
    void updateTodo_unauthorized() throws Exception{
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Completado");
        todo.setCompleted(true);

        mockMvc.perform(put("/api/todos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Delete todo return 200")
    @WithUserDetails("USER")
    void deleteTodo_success() throws Exception{
        mockMvc.perform(delete("/api/todos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete todo unauthorized return 401")
    void deleteTodo_unauthorized() throws Exception{
        mockMvc.perform(delete("/api/todos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}