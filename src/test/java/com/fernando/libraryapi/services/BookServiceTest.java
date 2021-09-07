package com.fernando.libraryapi.services;

import com.fernando.libraryapi.model.entities.Book;
import com.fernando.libraryapi.model.repositories.BookRepository;
import com.fernando.libraryapi.services.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBooktest() {
        // Arrange
        Book book = Book.builder().author("Fulano").title("Aprenda Spring").isbn("0001").build();
        Book mockedBook = Book.builder().id(1L).author("Fulano").title("Aprenda Spring").isbn("0001").build();
        when(repository.save(book)).thenReturn(mockedBook);

        // ACT
        Book savedBook = service.save(book);

        // Assert
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("0001");
        assertThat(savedBook.getTitle()).isEqualTo("Aprenda Spring");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");

    }
}
