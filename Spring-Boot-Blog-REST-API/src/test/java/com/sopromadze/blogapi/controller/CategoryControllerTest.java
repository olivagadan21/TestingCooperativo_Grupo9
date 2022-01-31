package com.sopromadze.blogapi.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestConfig;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestConfig.class})
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CategoryService categoryService;

    @Test
    @WithUserDetails("USER")
    @DisplayName("Add caregory return 200")
    void addCategory_success() throws Exception {
        Category category = new Category();

        mockMvc.perform(post("/api/categories")
                        .content(objectMapper.writeValueAsString(category))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @WithUserDetails("USER")
    @DisplayName("Add caregory return 400")
    void addCategory_badRequest () throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("Add caregory unauthorized, return 401")
    void addCategory_unauthorized () throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }


}