package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.configuration.TestDisableSecurityConfig;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.CategoryServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class, TestDisableSecurityConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CategoryServiceImpl categoryService;

    Category category;
    Page<Category> resultado;
    PagedResponse<Category> pagedResponse;
    Role rol;
    User user;
    UserPrincipal userPrincipal;
    Category newCategory;

    @BeforeEach
    void initData(){

        rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);

        List<Role> roles = Arrays.asList(rol);

        user = new User();
        user.setId(3L);
        user.setRoles(roles);

        userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        category = new Category();
        category.setName("Pure Saiyans");
        category.setCreatedBy(user.getId());

        newCategory = new Category();
        newCategory.setName("Hybrid Saiyans");
        newCategory.setCreatedBy(user.getId());
        newCategory.setCreatedAt(Instant.now());
        newCategory.setUpdatedAt(Instant.now());


        resultado = new PageImpl<>(Arrays.asList(category));

        List<Category> content = resultado.getNumberOfElements() == 0 ? Collections.emptyList() : resultado.getContent();


        pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(content);
        pagedResponse.setSize(resultado.getSize());
        pagedResponse.setSize(resultado.getSize());
        pagedResponse.setTotalElements(resultado.getTotalElements());
        pagedResponse.setTotalPages(resultado.getTotalPages());
        pagedResponse.setLast(resultado.isLast());
        pagedResponse.setPage(resultado.getNumber());


    }

    @Test
    void whenGetAllCategories_returns200() throws Exception {
        when(categoryService.getAllCategories(1, 10)).thenReturn(pagedResponse);
        mockMvc.perform(get("/api/categories")
                .param("page", "1")
                .param("size", "10")
                .contentType("application/json"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(pagedResponse)))
                .andExpect(status().isOk()).andDo(print());
    }

    @WithUserDetails(value = "admin")
    @Test
    void whenUpdateCategory_returns200() throws Exception{
        ResponseEntity<Category> responseEntity = new ResponseEntity<>(newCategory, HttpStatus.OK);

        when(categoryService.updateCategory(category.getId(), newCategory, userPrincipal)).thenReturn(responseEntity);

        mockMvc.perform(put("/api/categories/{id}", 1L)
                .content(objectMapper.writeValueAsString(newCategory))
                .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());

    }

    @Test
    void whenUpdateCategory_returns403() throws Exception{
        ResponseEntity<Category> responseEntity = new ResponseEntity<>(newCategory, HttpStatus.OK);

        when(categoryService.updateCategory(category.getId(), newCategory, userPrincipal)).thenReturn(responseEntity);

        mockMvc.perform(put("/api/categories/{id}", 1L)
                        .content(objectMapper.writeValueAsString(newCategory))
                        .contentType("application/json"))
                .andExpect(status().isForbidden()).andDo(print());

    }

}
