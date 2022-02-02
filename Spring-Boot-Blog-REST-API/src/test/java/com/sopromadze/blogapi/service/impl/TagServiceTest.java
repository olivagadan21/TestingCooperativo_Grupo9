package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.TagRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    Tag tag;
    Role rol;
    Role rol2;
    User user2;
    UserPrincipal userPrincipal;
    User user;
    UserPrincipal userPrincipal2;

    @BeforeEach
    void initData(){



        rol = new Role();
        rol.setName(RoleName.ROLE_ADMIN);
        rol2 = new Role();
        rol2.setName(RoleName.ROLE_USER);

        List<Role> roles1 = Arrays.asList(rol);

        List<Role> roles2 = Arrays.asList(rol2);

        user = new User();
        user.setId(1L);
        user.setUsername("DaTruth");
        user.setRoles(roles1);

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("Nanogenix");
        user2.setRoles(roles2);

        userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        userPrincipal2 = UserPrincipal.builder()
                .id(user2.getId())
                .username(user2.getUsername())
                .authorities(user2.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList()))
                .build();

        tag = new Tag();
        tag.setName("LR");
        tag.setCreatedBy(user.getId());

    }


    @Test
    void addTag_success(){
        when(tagRepository.save(tag)).thenReturn(tag);
        assertEquals(tag, tagService.addTag(tag, userPrincipal));
    }

    @Test
    void deleteTag_success(){
        when(tagRepository.findById(tag.getId())).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).deleteById(tag.getId());
        assertEquals(new ApiResponse(Boolean.TRUE, "You successfully deleted tag"), tagService.deleteTag(tag.getId(), userPrincipal));
    }

    @Test
    void deleteTag_throwsResourceNotFoundExceptionForTag(){

        when(tagRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()-> tagService.deleteTag(5L, userPrincipal));

    }

    @Test
    void deleteTag_throwsUnauthorizedException(){

        when(tagRepository.findById(tag.getId())).thenReturn(Optional.of(tag));
        assertThrows(UnauthorizedException.class, ()->tagService.deleteTag(tag.getId(), userPrincipal2));

    }

    /*
     * Test: Se comprueba que el método devuelve un Tag
     * Entrada: tagService.getTag(1L)
     * Salida esperada: Test se realiza con éxito
     */
    @Test
    @DisplayName("Get tag")
    void getTag_success() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("#Salesianos");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        assertEquals(tag, tagService.getTag(1L));
    }
    /*
     * Test: Se comprueba que el método lanza la excepción ResourceNotFoundException
     * Entrada: tagService.getTag(1L)
     * Salida esperada: Test se realiza con éxito y lanza la excepción ResourceNotFoundException
     */
    @Test
    @DisplayName("Get tag, content empty")
    void getTag_contentEmpty() {
        when(tagRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()-> tagService.getTag(1L));
    }

    @Test
    void getAllTags_success() {

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Tag");

        Page<Tag> tagPage = new PageImpl<>(Arrays.asList(tag));
        Page<Tag> tags = tagRepository.findAll(any(Pageable.class));

        PagedResponse<Tag> tagPagedResponse = new PagedResponse<>();
        tagPagedResponse.setContent(tagPage.getContent());
        tagPagedResponse.setTotalElements(1);
        tagPagedResponse.setLast(true);
        tagPagedResponse.setSize(1);
        tagPagedResponse.setTotalPages(1);

        when(tags).thenReturn(tagPage);

        assertEquals(tagPagedResponse, tagService.getAllTags(0,10));

    }

    @Test
    void updateTag_success() {

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


        when(tagRepository.findById(tag.getId())).thenReturn(Optional.of(tag));
        when(tagRepository.save(tag)).thenReturn(updated);

        assertEquals(true,userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        assertEquals(updated, tagService.updateTag(tag.getId(),tag, userPrincipal));

    }

    @Test
    void updateTag_ResourceNotFoundException_success() {

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


        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag));

        assertThrows(ResourceNotFoundException.class, () -> tagService.updateTag(tag.getId(),tag, userPrincipal));

    }

}
