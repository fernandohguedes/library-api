package com.fernando.libraryapi.model.repository;

import com.fernando.libraryapi.model.entities.Book;
import com.fernando.libraryapi.model.repositories.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
 class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    void returnTrueWhenIsbnExists() {
        // Arrange
        String isbn = "123";
        Book book = createNewBook(isbn);
        entityManager.persist(book);

        // Act
        boolean exists = repository.existsByIsbn(isbn);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base com o isbn informado")
    void returnFalseWhenIsbnDoesNotExist() {
        // Arrange
        String isbn = "123";

        // Act
        boolean exists = repository.existsByIsbn(isbn);

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve Obter um livro por Id")
    public void findByIdTest() {
        // Arrange
        Book book = createNewBook("123");
        entityManager.persist(book);

        // Act
        Optional<Book> foundBook = repository.findById(book.getId());

        // Assert
        assertThat(foundBook.isPresent()).isTrue();
    }

    private Book createNewBook(String isbn) {
        return Book.builder().author("Fulano").title("Aprenda Spring").isbn(isbn).build();
    }
}
