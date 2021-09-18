package com.fernando.libraryapi.services;

import com.fernando.libraryapi.exception.BusinessException;
import com.fernando.libraryapi.model.entities.Book;
import com.fernando.libraryapi.model.repositories.BookRepository;
import com.fernando.libraryapi.services.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
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
        Book book = createValidBook();
        Book mockedBook = Book.builder().id(1L).author("Fulano").title("Aprenda Spring").isbn("0001").build();
        when(repository.save(book)).thenReturn(mockedBook);
        when(repository.existsByIsbn(anyString())).thenReturn(false);

        // ACT
        Book savedBook = service.save(book);

        // Assert
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("0001");
        assertThat(savedBook.getTitle()).isEqualTo("Aprenda Spring");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com ISBN duplicado")
    public void souldNotSaveABookWithDuplocatedISBN() {
        // Arrange
        Book book = createValidBook();
        when(repository.existsByIsbn(anyString())).thenReturn(true);

        // ACT
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        // Assert
        assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrado");
        verify(repository, never()).save(any());
    }

    private Book createValidBook() {
        return Book.builder().author("Fulano").title("Aprenda Spring").isbn("0001").build();
    }
}
