package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.service.impl.TagServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
class TagControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TagServiceImpl tagService;

    @Test
    void getAllTags() throws Exception {

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Tag");

        Page<Tag> tagPage = new PageImpl<>(Arrays.asList(tag));

        PagedResponse<Tag> tagPagedResponse = new PagedResponse<>();
        tagPagedResponse.setContent(tagPage.getContent());
        tagPagedResponse.setTotalElements(1);
        tagPagedResponse.setLast(true);
        tagPagedResponse.setSize(1);
        tagPagedResponse.setTotalPages(1);

        when(tagService.getAllTags(1,1)).thenReturn(tagPagedResponse);

        mockMvc.perform(get("/api/tags", 1L)
                        .param("size","1").param("page","1")
                        .contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(tagPagedResponse)))
                .andExpect(status().isOk());

    }
}