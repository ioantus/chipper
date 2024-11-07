package com.itiutiaiev.chipper.model.dto

import groovy.transform.Canonical
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

import java.time.LocalDate

@Canonical
class UserUpdateDto {

    @NotEmpty
    @Size(min = 8, message = "Password can't be less then 8 characters")
    String password

    String firstName

    String lastName

    @NotNull(message = "Birthdate is mandatory")
    LocalDate birthdate

}
