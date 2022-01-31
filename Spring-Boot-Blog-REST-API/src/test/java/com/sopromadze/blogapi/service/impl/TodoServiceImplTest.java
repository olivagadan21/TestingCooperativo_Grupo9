package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.repository.TodoRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    @DisplayName("Complete todo")
    void completeTodo_success() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
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
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Todo completo");
        todo.setUser(user);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userRepository.getUser(userPrincipal)).thenReturn(user);

        todo.setCompleted(true);

        when(todoRepository.save(todo)).thenReturn(todo);
        assertEquals(todo, todoService.completeTodo(1L, userPrincipal));

    }

    @Test
    @DisplayName("Complete todo, todo not found")
    void completeTodo_todoNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
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

        when(todoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> todoService.completeTodo(1L,userPrincipal));
    }

    @Test
    @DisplayName("Complete todo, unauthorized")
    void completeTodo_unauthorizedException() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
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

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setUpdatedAt(Instant.now());
        secondUser.setEmail("alberto@gmail.com");
        secondUser.setPassword("12345678");
        secondUser.setFirstName("Alberto");
        secondUser.setCreatedAt(Instant.now());
        secondUser.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Todo completo");
        todo.setUser(secondUser);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        assertThrows(UnauthorizedException.class, ()-> todoService.completeTodo(1L, userPrincipal));
    }

    @Test
    @DisplayName("Update todo")
    void updateTodo_success() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
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

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Todo completo");
        todo.setUser(user);

        Todo newTodo = new Todo();
        newTodo.setTitle("Complete");
        newTodo.setCompleted(true);

        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        todo.setTitle(newTodo.getTitle());
        todo.setCompleted(newTodo.getCompleted());

        when(todoRepository.save(todo)).thenReturn(todo);
        assertEquals(todo,todoService.updateTodo(1L,newTodo, userPrincipal));
    }

    @Test
    @DisplayName("Update, todo not found")
    void updateTodo_notFound () {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
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

        when(todoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> todoService.completeTodo(1L,userPrincipal));
    }

    @Test
    @DisplayName("Update todo, unauthorized")
    void updateTodo_unauthorizedException() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
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

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setUpdatedAt(Instant.now());
        secondUser.setEmail("alberto@gmail.com");
        secondUser.setPassword("12345678");
        secondUser.setFirstName("Alberto");
        secondUser.setCreatedAt(Instant.now());
        secondUser.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Todo completo");
        todo.setUser(secondUser);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        assertThrows(UnauthorizedException.class, ()-> todoService.completeTodo(1L, userPrincipal));
    }


    @Test
    @DisplayName("Delete todo")
    void deleteTodo_success() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
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

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Todo completo");
        todo.setUser(user);

        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "You successfully deleted todo");

        assertEquals(apiResponse, todoService.deleteTodo(1L, userPrincipal));

    }

    @Test
    @DisplayName("Delete todo, unauthorized")
    void delete_unauthorizedException() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
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

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setUpdatedAt(Instant.now());
        secondUser.setEmail("alberto@gmail.com");
        secondUser.setPassword("12345678");
        secondUser.setFirstName("Alberto");
        secondUser.setCreatedAt(Instant.now());
        secondUser.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Todo completo");
        todo.setUser(secondUser);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        assertThrows(UnauthorizedException.class, ()-> todoService.completeTodo(1L, userPrincipal));
    }
    @Test
    @DisplayName("Delete, todo not found")
    void deleteTodo_notFound () {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_ADMIN);
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

        when(todoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> todoService.completeTodo(1L,userPrincipal));
    }

}