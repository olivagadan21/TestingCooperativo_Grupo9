package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.configuration.TestDisableSecurityConfig;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.TodoServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class, TestDisableSecurityConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoServiceImpl todoService;

    Todo todo;
    Role rol;
    Role rol2;
    User user2;
    UserPrincipal userPrincipal;
    User user;

    @BeforeEach
    void initData(){

        rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);
        rol2 = new Role();
        rol2.setName(RoleName.ROLE_USER);

        List<Role> roles1 = Arrays.asList(rol);

        List<Role> roles2 = Arrays.asList(rol2);

        user = new User();
        user.setId(3L);
        user.setUsername("DaTruth");
        user.setRoles(roles1);
        user.setCreatedAt(Instant.now());

        userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();


        todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Farm Super Saiyan God Goku & Super Saiyan God Vegeta Medals");
        todo.setUser(user);

    }

    @WithMockUser(authorities = {"ROLE_USER"})
    @Test
    void whenUnCompleteTodo_returns200() throws Exception {

        when(todoService.unCompleteTodo(todo.getId(), userPrincipal)).thenReturn(todo);

        mockMvc.perform(put("/api/todos/{id}/unComplete", 1L)
                        .content(objectMapper.writeValueAsString(todo))
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());


    }

    @Test
    void whenUnCompleteTodo_returns403() throws Exception {

        when(todoService.unCompleteTodo(todo.getId(), userPrincipal)).thenReturn(todo);

        mockMvc.perform(put("/api/todos/{id}/unComplete", 1L)
                        .content(objectMapper.writeValueAsString(todo))
                        .contentType("application/json"))
                .andExpect(status().isForbidden()).andDo(print());


    }

    @WithMockUser(authorities = {"ROLE_USER"})
    @Test
    void whenAddTodo_returns201() throws Exception {

        when(todoService.addTodo(todo, userPrincipal)).thenReturn(todo);

        mockMvc.perform(post("/api/todos")
                        .content(objectMapper.writeValueAsString(todo))
                        .contentType("application/json"))
                .andExpect(status().isCreated()).andDo(print());


    }

    @Test
    void whenAddTodo_returns403() throws Exception {

        when(todoService.addTodo(todo, userPrincipal)).thenReturn(todo);

        mockMvc.perform(post("/api/todos")
                        .content(objectMapper.writeValueAsString(todo))
                        .contentType("application/json"))
                .andExpect(status().isForbidden()).andDo(print());


    }



}
