package com.itiutiaiev.chipper.model.entity

import groovy.transform.Canonical
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

import java.time.LocalDate
import java.time.LocalDateTime

@Document(collection = "users")
@Canonical
class User {

    @MongoId
    ObjectId id

    @Indexed(unique = true)
    @Email(message = "Email is incorrect")
    String email

    @NotNull(message = "Password is mandatory")
    String password

    @Size(max = 50, message = "First name must not exceed 50 characters")
    String firstName

    @Size(max = 50, message = "First name must not exceed 50 characters")
    String lastName

    @NotNull(message = "Birthdate is mandatory")
    LocalDate birthdate

    Set<String> subscriptions = []
    Set<String> subscribers = []

    boolean deleted = false

    @CreatedDate
    LocalDateTime dateCreated

    @LastModifiedDate
    LocalDateTime lastModified

}
