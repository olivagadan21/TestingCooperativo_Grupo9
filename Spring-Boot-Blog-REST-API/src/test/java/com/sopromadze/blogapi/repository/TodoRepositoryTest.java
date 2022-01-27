package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class TodoRepositoryTest {

    @MockBean
    TodoRepository todoRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    void testNotNull() {
        assertNotNull(todoRepository);
    }

    @Test
    public void findByCreatedBy_success() {

        User user = new User();
        user.setFirstName("Jesús");
        user.setLastName("Barco");
        user.setUsername("jesusbarco02");
        user.setPassword("12345678");
        user.setEmail("jesusbarco@gmail.com");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        Todo todo = new Todo();
        todo.setTitle("Título");
        todo.setCreatedAt(Instant.now());
        todo.setUpdatedAt(Instant.now());
        todo.setUser(user);

        testEntityManager.persist(todo);

        Page<Todo> todos = new PageImpl<>(Arrays.asList(todo));

        PageRequest pageRequest = PageRequest.of(1,10);

        when(todoRepository.findByCreatedBy(user.getId(), pageRequest)).thenReturn(todos);

        assertNotEquals(0, todoRepository.findByCreatedBy(user.getId(), pageRequest).getTotalElements());

    }

}
