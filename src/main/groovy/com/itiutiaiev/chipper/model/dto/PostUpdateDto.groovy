package com.itiutiaiev.chipper.model.dto

import groovy.transform.Canonical
import jakarta.validation.constraints.NotBlank

@Canonical
class PostUpdateDto extends PostCreateDto {

    @NotBlank(message = "Id is mandatory")
    String _id

}
