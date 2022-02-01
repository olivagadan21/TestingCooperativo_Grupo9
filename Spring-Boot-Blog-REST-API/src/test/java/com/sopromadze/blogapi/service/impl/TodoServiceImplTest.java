package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.TodoRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TodoServiceImplTest {

    @Mock
    TodoRepository todoRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    TodoServiceImpl todoService;

    @Test
    void getAllTodos_success() {

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

        Todo todo = new Todo();
        todo.setTitle("Título");
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
        todo.setUser(user);
        todoRepository.save(todo);

        Page<Todo> todoPage = new PageImpl<>(Arrays.asList(todo));
        Page<Todo> todos = todoRepository.findAll(any(Pageable.class));

        PagedResponse<Todo> todoPagedResponse = new PagedResponse<>();
        todoPagedResponse.setContent(todoPage.getContent());
        todoPagedResponse.setTotalElements(1);
        todoPagedResponse.setLast(true);
        todoPagedResponse.setSize(1);
        todoPagedResponse.setTotalPages(1);

        when(todos).thenReturn(todoPage);

        assertEquals(todoPagedResponse, todoService.getAllTodos(userPrincipal,0, 10));

    }

    @Test
    void getTodo_success() {

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

        Todo todo = new Todo();
        todo.setTitle("Título");
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
        todo.setUser(user);

        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

        assertEquals(true,userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        assertEquals(todo, todoService.getTodo(todo.getId(), userPrincipal));

    }

    @Test
    void getTodo_ResourceNotFoundException_success() {

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
        userRepository.save(user);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Todo todo = new Todo();
        todo.setTitle("Título");
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
        todo.setUser(user);

        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        when(todoRepository.findById(2L)).thenReturn(Optional.of(todo));

        assertEquals(true,userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        assertThrows(ResourceNotFoundException.class, () -> todoService.getTodo(todo.getId(), userPrincipal));

    }

}