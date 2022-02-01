package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.TodoServiceImpl;
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

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT, classes =
        {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoServiceImpl todoService;
/*
    @Test
    @DisplayName("Get todo return 200")
    void getTodo_success() throws Exception {

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(roleList);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Todo todo = new Todo();
        todo.setTitle("Título");
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
        todo.setUser(user);

        when(todoService.getTodo(todo.getId(), userPrincipal)).thenReturn(todo);

        mockMvc.perform(get("/api/todos/{id}",1L,1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk());

    }

    @Test
    @WithUserDetails("USER")
    @DisplayName("Get todos return 200")
    void getAllTodos_success() throws Exception {

        Role rol = new Role();
        rol.setName(RoleName.ROLE_USER);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(roleList);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Todo todo = new Todo();
        todo.setTitle("Título");
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
        todo.setUser(user);

        Page<Todo> todoPage = new PageImpl<>(Arrays.asList(todo));

        PagedResponse<Todo> todoPagedResponse = new PagedResponse<>();
        todoPagedResponse.setContent(todoPage.getContent());
        todoPagedResponse.setTotalElements(1);
        todoPagedResponse.setLast(true);
        todoPagedResponse.setSize(1);
        todoPagedResponse.setTotalPages(1);

        when(todoService.getAllTodos(userPrincipal, 1,1)).thenReturn(todoPagedResponse);

        mockMvc.perform(get("/api/todos", 1L)
                        .param("size","1").param("page","1")
                        .contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(todoPage)))
                .andExpect(status().isOk());

    }

 */
}