package com.fernando.libraryapi.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Book {

    private Long id;
    private String title;
    private String author;
    private String isbn;

}
