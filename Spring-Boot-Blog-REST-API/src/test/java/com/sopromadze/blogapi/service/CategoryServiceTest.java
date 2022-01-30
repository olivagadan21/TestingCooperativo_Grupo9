package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    Pageable pageable;
    Category category;
    Category newCategory;
    Page<Category> resultado;
    PagedResponse<Category> pagedResponse;
    Role rol;
    User user;
    UserPrincipal userPrincipal;

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



        pageable = PageRequest.of(1, 10, Sort.Direction.DESC, "createdAt");

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
    void getAllCategories_success(){

        when(categoryRepository.findAll(pageable)).thenReturn(resultado);

        assertEquals(pagedResponse, categoryService.getAllCategories(1, 10));


    }

    @Test
    void updateCategory_success(){
        ResponseEntity<Category> responseEntity = new ResponseEntity<>(newCategory, HttpStatus.OK);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.save(newCategory)).thenReturn(newCategory);
        assertEquals(responseEntity, categoryService.updateCategory(category.getId(), newCategory, userPrincipal));
    }

    @Test
    void updateCategory_throwsResourceNotFoundExceptionForCategory(){

        when(categoryRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()->categoryService.updateCategory(5L, newCategory, userPrincipal));
    }

}
