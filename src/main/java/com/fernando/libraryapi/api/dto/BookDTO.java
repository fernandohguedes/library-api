package com.fernando.libraryapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookDTO {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String author;

    @NotNull
    private String isbn;


}
