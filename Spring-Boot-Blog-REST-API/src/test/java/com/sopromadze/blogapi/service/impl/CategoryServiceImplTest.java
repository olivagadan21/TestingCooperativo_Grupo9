package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryServiceImplTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Test
    void getCategory_access() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Viaje");

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        assertEquals(category, categoryService.getCategory(category.getId()).getBody());

    }

    @Test
    void getCategory_ResourceNotFoundException_access() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Viaje");

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        assertThrows(ResourceNotFoundException.class, ()->categoryService.getCategory(category.getId()).getBody());

    }

    @Test
    void deleteCategory_access() {

        Role admin = new Role();
        admin.setId(1L);
        admin.setName(RoleName.ROLE_ADMIN);
        List<Role> listRole = new ArrayList<>();
        listRole.add(admin);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Category category = new Category();
        category.setId(1L);
        category.setName("Viaje");
        category.setCreatedBy(1L);
        category.setUpdatedBy(1L);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "You successfully deleted category");

        assertEquals(true,userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        assertEquals(apiResponse, categoryService.deleteCategory(category.getId(), userPrincipal).getBody());

    }

    @Test
    void deleteCategory_ResourceNotFoundException_access() {

        Role admin = new Role();
        admin.setId(1L);
        admin.setName(RoleName.ROLE_ADMIN);
        List<Role> listRole = new ArrayList<>();
        listRole.add(admin);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Category category = new Category();
        category.setId(1L);
        category.setName("Viaje");
        category.setCreatedBy(1L);
        category.setUpdatedBy(1L);

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "You successfully deleted category");

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(category.getId(), userPrincipal).getBody());

    }

}