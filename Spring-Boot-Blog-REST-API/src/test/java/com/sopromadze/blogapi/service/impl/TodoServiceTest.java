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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    Todo todo2;
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
        todo.setUser(user);


        todo2 = new Todo();
        todo2.setId(2L);
        todo2.setUser(user2);




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
        when(todoRepository.findById(todo2.getId())).thenReturn(Optional.of(todo2));
        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        System.out.println(todo2.getUser().getId());
        System.out.println(userPrincipal.getId());
        assertThrows(UnauthorizedException.class, ()->todoService.unCompleteTodo(todo2.getId(), userPrincipal));
    }

    @Test
    void addTodo_success(){
        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        when(todoRepository.save(todo)).thenReturn(todo);
        assertEquals(todoRepository.save(todo), todoService.addTodo(todo, userPrincipal));

    }


    /*
     * Test: Se comprueba que el m??todo devuelve 1TODO
     * Entrada: todoService.completeTodo(1L, userPrincipal)
     * Salida esperada: Test se realiza con ??xito
     */
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
        user.setFirstName("Jes??s");
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
    /*
     * Test: Se comprueba que el m??todo lanza la excepci??n ResourceNotFoundException
     * Entrada: todoService.completeTodo(1L, userPrincipal)
     * Salida esperada: Test se realiza con ??xito y lanza la excepci??n ResourceNotFoundException
     */
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
        user.setFirstName("Jes??s");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        when(todoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> todoService.completeTodo(1L,userPrincipal));
    }
    /*
     * Test: Se comprueba que el m??todo lanza la excepci??n UnauthorizedException
     * Entrada: todoService.completeTodo(1L, userPrincipal)
     * Salida esperada: Test se realiza con ??xito y lanza la excepci??n UnauthorizedException
     */
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
        user.setFirstName("Jes??s");
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
    /*
     * Test: Se comprueba que el m??todo devuelve 1Todo
     * Entrada: todoService.updateTodo(1L,newTodo, userPrincipal)
     * Salida esperada: Test se realiza con ??xito
     */
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
        user.setFirstName("Jes??s");
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
    /*
     * Test: Se comprueba que el m??todo lanza la excepci??n ResourceNotFoundException
     * Entrada: todoService.updateTodo(1L,newTodo, userPrincipal)
     * Salida esperada: Test se realiza con ??xito y lanza la excepci??n ResourceNotFoundException
     */
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
        user.setFirstName("Jes??s");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        when(todoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> todoService.completeTodo(1L,userPrincipal));
    }
    /*
     * Test: Se comprueba que el m??todo lanza la excepci??n UnauthorizedException
     * Entrada: todoService.updateTodo(1L,newTodo, userPrincipal)
     * Salida esperada: Test se realiza con ??xito y lanza la excepci??n UnauthorizedException
     */
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
        user.setFirstName("Jes??s");
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

    /*
     * Test: Se comprueba que el m??todo elimina ApiResponse
     * Entrada: todoService.deleteTodo(1L, userPrincipal)
     * Salida esperada: Test se realiza con ??xito
     */
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
        user.setFirstName("Jes??s");
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
    /*
     * Test: Se comprueba que el m??todo lanza la excepci??n UnauthorizedException
     * Entrada: todoService.deleteTodo(1L, userPrincipal)
     * Salida esperada: Test se realiza con ??xito y lanza la excepci??n UnauthorizedException
     */
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
        user.setFirstName("Jes??s");
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

    /*
     * Test: Se comprueba que el m??todo lanza la excepci??n ResourceNotFoundException
     * Entrada: todoService.deleteTodo(1L, userPrincipal)
     * Salida esperada: Test se realiza con ??xito y lanza la excepci??n ResourceNotFoundException
     */
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
        user.setFirstName("Jes??s");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        when(todoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> todoService.completeTodo(1L,userPrincipal));
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
        todo.setTitle("T??tulo");
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
        todo.setTitle("T??tulo");
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
        todo.setUser(user);

        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        when(todoRepository.findById(2L)).thenReturn(Optional.of(todo));

        assertEquals(true,userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        assertThrows(ResourceNotFoundException.class, () -> todoService.getTodo(todo.getId(), userPrincipal));

    }

}
