package com.fernando.libraryapi.api.resources;

import com.fernando.libraryapi.api.dto.BookDTO;
import com.fernando.libraryapi.api.exceptions.ApiErrors;
import com.fernando.libraryapi.model.entities.Book;
import com.fernando.libraryapi.services.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@Valid @RequestBody BookDTO dto) {
        Book book = modelMapper.map(dto, Book.class);
        book = service.save(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        return new ApiErrors(bindingResult);
    }
}