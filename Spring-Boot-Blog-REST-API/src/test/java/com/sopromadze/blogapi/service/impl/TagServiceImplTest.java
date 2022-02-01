package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TagServiceImplTest {

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagServiceImpl tagService;

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
}