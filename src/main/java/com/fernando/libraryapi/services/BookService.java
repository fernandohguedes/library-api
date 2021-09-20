package com.fernando.libraryapi.services;

import com.fernando.libraryapi.model.entities.Book;

import java.util.Optional;

public interface BookService {

    Book save(Book any);

    Optional<Book> getById(Long id);
}
