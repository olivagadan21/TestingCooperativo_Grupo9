package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    void getAllComments_success(){

        Post post = new Post();
        post.setTitle("Post buenardo");
        post.setId(1L);

        Comment comment = new Comment();
        comment.setName("Pepe");
        comment.setPost(post);

        Page<Comment> resultado = new PageImpl<>(Arrays.asList(comment));

        PagedResponse<Comment> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(resultado.getContent());
        pagedResponse.setTotalPages(1);
        pagedResponse.setTotalElements(1);
        pagedResponse.setLast(true);
        pagedResponse.setSize(1);

        Pageable pageable = PageRequest.of(1, 10);

        when(commentRepository.findByPostId(any(Long.class), any(Pageable.class))).thenReturn(resultado);

        assertEquals(pagedResponse, commentService.getAllComments(1L, 1, 10));


    }

}
