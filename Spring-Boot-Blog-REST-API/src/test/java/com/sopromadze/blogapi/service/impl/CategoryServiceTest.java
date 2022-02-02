package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CategoryServiceTest {

    @Mock
    UserRepository userRepository;

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

    /*
     * Test: Se comprueba que el método devuelve ResponseEntity<Category>
     * Entrada: categoryService.addCategory(category, userPrincipal)
     * Salida esperada: Test se realiza con éxito y devuelve ResponseEntity<Category>
     */
    @Test
    @DisplayName("Add category")
    void addCategory_success() {
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
        user.setFirstName("Jesús");
        user.setCreatedAt(Instant.now());
        user.setRoles(listRole);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        Category category = new Category();
        category.setId(1L);
        category.setName("Fotos de verano");

        when(userRepository.getUserByName(userPrincipal.getUsername())).thenReturn(user);
        when(categoryRepository.save(category)).thenReturn(category);

        ResponseEntity responseEntity = new ResponseEntity(category, HttpStatus.CREATED);
        assertEquals(responseEntity,categoryService.addCategory(category, userPrincipal));
    }

    @Test
    void getCategory_success() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Viaje");

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        assertEquals(category, categoryService.getCategory(category.getId()).getBody());

    }

    @Test
    void getCategory_ResourceNotFoundException_success() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Viaje");

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        assertThrows(ResourceNotFoundException.class, ()->categoryService.getCategory(category.getId()).getBody());

    }

    @Test
    void deleteCategory_success() {

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
    void deleteCategory_ResourceNotFoundException_success() {

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
