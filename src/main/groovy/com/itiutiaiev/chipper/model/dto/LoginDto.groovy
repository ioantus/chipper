package com.itiutiaiev.chipper.model.dto

import groovy.transform.Canonical
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

@Canonical
class LoginDto {

    @Email(message = "Email is incorrect")
    String email

    @NotEmpty
    @Size(min = 8, message = "Password can't be less then 8 characters")
    String password
}
