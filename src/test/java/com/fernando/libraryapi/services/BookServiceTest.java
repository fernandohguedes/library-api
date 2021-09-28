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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    void saveBooktest() {
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
    void souldNotSaveABookWithDuplocatedISBN() {
        // Arrange
        Book book = createValidBook();
        when(repository.existsByIsbn(anyString())).thenReturn(true);

        // ACT
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        // Assert
        assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrado");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void getByIdtest() {
        // Arrange
        Long id = 1l;

        Book book = createValidBook();
        book.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(book));

        // ACT
        Optional<Book> foundBook = service.getById(id);

        // Assert
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void getBookNotFoundByIdtest() {
        // Arrange
        Long id = 1l;

        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT
        Optional<Book> foundBook = service.getById(id);

        // Assert
        assertThat(foundBook.isPresent()).isFalse();
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() {
        // Arrange
        Book book = Book.builder().id(1L).build();

        // ACT
        assertDoesNotThrow(() -> service.delete(book));

        // Assert
        verify(repository, times(1)).delete(any());
    }

    @Test
    @DisplayName("Deve deletar lançar exception IllegalArgumentException quando o livro estiver nulo")
    public void deleteBookInvalidBooTest() {
        // Assert
        Book book = null;

        // ACT
        Throwable exception = Assertions.catchThrowable(() -> service.delete(book));

        // Assert
        assertThat(exception).isInstanceOf(IllegalArgumentException.class).hasMessage("Book id cant be null");
        verify(repository, never()).delete(any());
    }


    private Book createValidBook() {
        return Book.builder().author("Fulano").title("Aprenda Spring").isbn("0001").build();
    }
}
