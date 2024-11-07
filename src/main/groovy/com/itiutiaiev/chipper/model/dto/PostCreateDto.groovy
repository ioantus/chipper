package com.itiutiaiev.chipper.model.dto

import groovy.transform.Canonical
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Canonical
class PostCreateDto {

    @NotBlank(message = "Title is mandatory")
    String title

    @NotNull(message = "Body is mandatory")
    byte[] body

}
