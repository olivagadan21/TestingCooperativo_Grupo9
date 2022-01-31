package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TagServiceImplTest {

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagServiceImpl tagService;

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