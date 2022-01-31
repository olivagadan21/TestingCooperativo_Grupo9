package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AlbumServiceImplTest {

    @Mock
    AlbumRepository albumRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    AlbumServiceImpl albumService;

    @Test
    void addAlbum_success() {

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

        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setId(1L);
        albumRequest.setTitle("Viaje a Francia.");
        albumRequest.setUser(user);

        when(userRepository.getUser(userPrincipal)).thenReturn(user);

        Album album = new Album();
        album.setId(albumRequest.getId());
        album.setTitle(albumRequest.getTitle());
        album.setUser(user);

        when(albumRepository.save(any(Album.class))).thenReturn(album);

        assertEquals(album, albumService.addAlbum(albumRequest, userPrincipal).getBody());

    }

    @Test
    void updateAlbum_success() {

        Role rol = new Role();
        rol.setId(1L);
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

        when(userRepository.getUser(userPrincipal)).thenReturn(user);

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Viaje a Francia.");
        album.setUser(user);

        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setId(album.getId());
        albumRequest.setTitle("Viaje a París");
        albumRequest.setUser(user);

        Album updated = new Album();
        updated.setId(album.getId());
        updated.setTitle(albumRequest.getTitle());
        updated.setUser(albumRequest.getUser());

        AlbumResponse albumResponse = new AlbumResponse();
        albumResponse.setId(updated.getId());
        albumResponse.setTitle(updated.getTitle());
        albumResponse.setUser(updated.getUser());

        when(albumRepository.findById(album.getId())).thenReturn(Optional.of(album));
        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        when(albumRepository.save(any(Album.class))).thenReturn(updated);

        assertNotEquals(albumResponse, albumService.updateAlbum(album.getId(),albumRequest, userPrincipal).getBody());

    }

    @Test
    void updateAlbum_ResourceNotFoundException_success() {

        Role rol = new Role();
        rol.setId(1L);
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

        when(userRepository.getUser(userPrincipal)).thenReturn(user);

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Viaje a Francia.");
        album.setUser(user);

        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setId(album.getId());
        albumRequest.setTitle("Viaje a París");
        albumRequest.setUser(user);

        Album updated = new Album();
        updated.setId(album.getId());
        updated.setTitle(albumRequest.getTitle());
        updated.setUser(albumRequest.getUser());

        AlbumResponse albumResponse = new AlbumResponse();
        albumResponse.setId(updated.getId());
        albumResponse.setTitle(updated.getTitle());
        albumResponse.setUser(updated.getUser());

        when(albumRepository.findById(2L)).thenReturn(Optional.of(album));

        assertThrows(ResourceNotFoundException.class, () -> albumService.updateAlbum(album.getId(),albumRequest, userPrincipal).getBody());

    }

    @Test
    void updateAlbum_BlogapiException_success() {

        Role rol = new Role();
        rol.setId(1L);
        rol.setName(RoleName.ROLE_USER);

        List<Role> roleList = Arrays.asList(rol);

        User user = new User();
        user.setId(1L);
        user.setEmail("danieloliva@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Daniel");
        user.setLastName("Oliva");
        user.setRoles(roleList);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("danieloliva@gmail.com");
        user2.setPassword("12345678");
        user2.setFirstName("Daniel");
        user2.setLastName("Oliva");
        user2.setRoles(roleList);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        when(userRepository.getUser(userPrincipal)).thenReturn(user);

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Viaje a Francia.");
        album.setUser(user2);

        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setId(album.getId());
        albumRequest.setTitle("Viaje a París");
        albumRequest.setUser(user2);

        Album updated = new Album();
        updated.setId(album.getId());
        updated.setTitle(albumRequest.getTitle());
        updated.setUser(albumRequest.getUser());

        AlbumResponse albumResponse = new AlbumResponse();
        albumResponse.setId(updated.getId());
        albumResponse.setTitle(updated.getTitle());
        albumResponse.setUser(updated.getUser());

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(userRepository.getUser(userPrincipal)).thenReturn(user);
        when(albumRepository.save(any(Album.class))).thenReturn(updated);

        assertNotEquals(album.getUser().getId(), userPrincipal.getId() );
        assertThrows(BlogapiException.class, () -> albumService.updateAlbum(album.getId(),albumRequest, userPrincipal).getBody());

    }

    @Test
    void deleteAlbum_success() {

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

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Viaje de fin de curso");
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());
        album.setUser(user);
        albumRepository.save(album);

        when(albumRepository.findById(album.getId())).thenReturn(Optional.of(album));
        when(userRepository.getUser(userPrincipal)).thenReturn(user);

        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "You successfully deleted album");

        assertEquals(true,userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        assertEquals(apiResponse, albumService.deleteAlbum(album.getId(), userPrincipal).getBody());

    }

    @Test
    void deleteAlbum_ResourceNotFoundException_success() {

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

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Viaje de fin de curso");
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());
        album.setUser(user);
        albumRepository.save(album);

        when(albumRepository.findById(2L)).thenReturn(Optional.of(album));

        assertThrows(ResourceNotFoundException.class, ()->albumService.deleteAlbum(album.getId(), userPrincipal).getBody());

    }

}