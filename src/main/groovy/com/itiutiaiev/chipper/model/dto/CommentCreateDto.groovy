package com.itiutiaiev.chipper.model.dto

import groovy.transform.Canonical
import jakarta.validation.constraints.NotBlank

@Canonical
class CommentCreateDto {

    @NotBlank(message = "Text is mandatory")
    String text

}
