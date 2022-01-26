package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class TodoRepositoryTest {

    @Autowired
    TodoRepository todoRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void findByCreatedBy_success() {

        User user = new User();
        user.setId(1L);
        user.setFirstName("Jesús");
        user.setLastName("Barco");
        user.setUsername("jesusbarco02");
        user.setPassword("12345678");
        user.setEmail("jesusbarco@gmail.com");
        userRepository.save(user);

        Todo todo = new Todo();
        todo.setTitle("Título");
        todoRepository.save(todo);

        Page<Todo> todoPage = new PageImpl<>(Arrays.asList(todo));

        Page<Todo> todos = todoRepository.findByCreatedBy(1L, any(Pageable.class));

        assertEquals(todoPage, todos);

    }

    @Test
    public void findByCreatedByNonExisting_success() {

        Page<Todo> todos = todoRepository.findByCreatedBy(1L, any(Pageable.class));

        assertEquals(0, todos.getTotalElements());

    }

}
