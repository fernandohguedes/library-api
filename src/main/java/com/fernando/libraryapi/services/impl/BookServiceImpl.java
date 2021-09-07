package com.fernando.libraryapi.services.impl;

import com.fernando.libraryapi.model.entities.Book;
import com.fernando.libraryapi.model.repositories.BookRepository;
import com.fernando.libraryapi.services.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        book = repository.save(book);
        return book;
    }

}
