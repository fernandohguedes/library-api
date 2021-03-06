package com.fernando.libraryapi.api.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernando.libraryapi.api.dto.BookDTO;
import com.fernando.libraryapi.exception.BusinessException;
import com.fernando.libraryapi.model.entities.Book;
import com.fernando.libraryapi.services.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
class BookControllerTest {

    private static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    void createBookTest() throws Exception {
        BookDTO book = createBookDTO();

        Book savedBook = Book.builder().id(10l).author("José").title("Aprenda Spring").isbn("001").build();
        given(service.save(ArgumentMatchers.any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(book);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    @DisplayName("Deve lançar erro de validação ao criar livro")
    void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro")
    void createdBookWithDuplicatedIsbn() throws Exception {
        String message = "Isbn já cadastrado";
        BookDTO book = createBookDTO();
        String json = new ObjectMapper().writeValueAsString(book);

        given(service.save(any(Book.class))).willThrow(new BusinessException(message));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(message));
    }

    @Test
    @DisplayName("Deve obter informações de um livro")
    void getBookDetails() throws Exception {
        // cenário (given)
        Long id = 1L;

        Book book = Book.builder().id(1L).title(createBookDTO().getTitle()).author(createBookDTO().getAuthor()).isbn(createBookDTO().getIsbn()).build();
        given(service.getById(id)).willReturn(Optional.of(book));

        // execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createBookDTO().getTitle()))
                .andExpect(jsonPath("author").value(createBookDTO().getAuthor()))
                .andExpect(jsonPath("isbn").value(createBookDTO().getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir")
    void bookNotFound() throws Exception {
        // cenário (given)
        given(service.getById(anyLong())).willReturn(Optional.empty());

        // execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + anyLong()))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar o livro parar deletar")
    public void deleteBookNotFound() throws Exception {
        // cenário (given)
        given(service.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBook() throws Exception {
        // cenário (given)
        given(service.getById(anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBook() throws Exception {
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createBookDTO());

        Book updatingBook = Book.builder().id(1l).title("some title").author("some author").isbn("321").build();
        given(service.getById(id)).willReturn(Optional.of(updatingBook));

        Book updatedBook = Book.builder().id(1l).author("José").title("Aprenda Spring").isbn("001").build();
        given(service.update(updatingBook)).willReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createBookDTO().getTitle()))
                .andExpect(jsonPath("author").value(createBookDTO().getAuthor()))
                .andExpect(jsonPath("isbn").value("001"));

    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar o livro parar atualizar")
    public void updateBookNotFound() throws Exception {
        String json = new ObjectMapper().writeValueAsString(createBookDTO());

        given(service.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());

    }



    private BookDTO createBookDTO() {
        return BookDTO.builder().author("José").title("Aprenda Spring").isbn("001").build();
    }
}
