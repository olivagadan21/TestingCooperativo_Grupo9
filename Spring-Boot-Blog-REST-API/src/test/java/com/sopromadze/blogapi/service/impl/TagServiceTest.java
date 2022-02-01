package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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


}
