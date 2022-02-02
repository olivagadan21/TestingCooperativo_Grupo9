package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.configuration.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.configuration.TestDisableSecurityConfig;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.TagServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class, TestDisableSecurityConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TagServiceImpl tagService;


    Tag tag;
    Role rol;
    Role rol2;
    User user;

    UserPrincipal userPrincipal;

    @BeforeEach
    void initData(){

        tag = new Tag();
        tag.setName("LR");

        rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);
        rol2 = new Role();
        rol2.setName(RoleName.ROLE_USER);

        List<Role> roles = Arrays.asList(rol, rol2);

        user = new User();
        user.setUsername("DaTruth");
        user.setRoles(roles);

        userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

    }

    @WithMockUser(authorities = {"ROLE_USER"})
    @Test
    void whenAddTag_returns201() throws Exception{

        when(tagService.addTag(tag, userPrincipal)).thenReturn(tag);

        mockMvc.perform(post("/api/tags")
                        .content(objectMapper.writeValueAsString(new ResponseEntity< >(tag, HttpStatus.CREATED)))
                        .contentType("application/json"))
                .andExpect(status().isCreated()).andDo(print());


    }

    @Test
    void whenAddTag_returns403() throws Exception{

        when(tagService.addTag(tag, userPrincipal)).thenReturn(tag);

        mockMvc.perform(post("/api/tags")
                        .content(objectMapper.writeValueAsString(new ResponseEntity< >(tag, HttpStatus.CREATED)))
                        .contentType("application/json"))
                .andExpect(status().isForbidden()).andDo(print());


    }
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @Test
    void whenDeleteTag_returns200() throws Exception {
        when(tagService.deleteTag(tag.getId(), userPrincipal)).thenReturn(new ApiResponse(Boolean.TRUE, "You successfully deleted tag"));
        mockMvc.perform(delete("/api/tags/{id}", 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk()).andDo(print());


    }
    @Test
    void whenDeleteTag_returns403() throws Exception {
        when(tagService.deleteTag(tag.getId(), userPrincipal)).thenReturn(new ApiResponse(Boolean.TRUE, "You successfully deleted tag"));
        mockMvc.perform(delete("/api/tags/{id}", 1L)
                        .contentType("application/json"))
                .andExpect(status().isForbidden()).andDo(print());


    }

    @Test
    @DisplayName("Get tag return 200")
    void getTag_success() throws Exception {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("#Salesianos");

        when(tagService.getTag(1L)).thenReturn(tag);
        mockMvc.perform(get("/api/tags/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isOk());
    }

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

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @DisplayName("Update tag return 200")
    void updateTag_success() throws Exception {

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

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Ta");
        tag.setCreatedBy(1L);
        tag.setUpdatedBy(1L);

        Tag updated = new Tag();
        updated.setId(tag.getId());
        updated.setName("Tag");
        updated.setCreatedBy(tag.getCreatedBy());
        updated.setUpdatedBy(1L);

        when(tagService.updateTag(tag.getId(), updated, userPrincipal)).thenReturn(updated);

        mockMvc.perform(put("/api/tags/1", 1L)
                        .content(objectMapper.writeValueAsString(updated))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}
