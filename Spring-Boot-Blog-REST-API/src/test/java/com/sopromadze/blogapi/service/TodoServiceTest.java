package com.sopromadze.blogapi.service;

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
import com.sopromadze.blogapi.service.impl.TodoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sopromadze.blogapi.utils.AppConstants.YOU_DON_T_HAVE_PERMISSION_TO_MAKE_THIS_OPERATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoServiceImpl todoService;


    Todo todo;
    Role rol;
    Role rol2;
    User user2;
    UserPrincipal userPrincipal;
    User user;
    UserPrincipal userPrincipal2;
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

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("Nanogenix");
        user2.setRoles(roles2);
        user2.setCreatedAt(Instant.now());

        userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        userPrincipal2 = UserPrincipal.builder()
                .id(user2.getId())
                .username(user2.getUsername())
                .authorities(user2.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();


        todo = new Todo();
        todo.setId(1L);
        todo.setUser(user2);




    }

    @Test
    void unCompleteTodo_success(){
        when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));
        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        when(todoRepository.save(todo)).thenReturn(todo);
        assertEquals(todoService.unCompleteTodo(todo.getId(), userPrincipal), todo);
    }

    @Test
    void unCompleteTodo_throwsResourceNotFoundExceptionForTodo(){
        when(todoRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->todoService.unCompleteTodo(3L, userPrincipal));
    }

    @Test
    void unCompleteTodo_throwsUnauthorizedException(){
        when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));
        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        System.out.println(todo.getUser().getId());
        System.out.println(userPrincipal.getId());
        assertThrows(UnauthorizedException.class, ()->todoService.unCompleteTodo(todo.getId(), userPrincipal));
    }

    @Test
    void addTodo_success(){
        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        when(todoRepository.save(todo)).thenReturn(todo);
        assertEquals(todoRepository.save(todo), todoService.addTodo(todo, userPrincipal));

    }




}
