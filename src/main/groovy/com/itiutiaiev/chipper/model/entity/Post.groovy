package com.itiutiaiev.chipper.model.entity

import groovy.transform.Canonical
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import org.springframework.data.mongodb.core.mapping.MongoId

import java.time.LocalDateTime

@Document(collection = "posts")
@Canonical
class Post {

    @MongoId
    ObjectId id

    @Indexed
    @NotBlank(message = "Author is mandatory")
    String authorEmail

    @NotBlank(message = "Title field is mandatory")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    String title

    @NotNull(message = "Post body is mandatory")
    byte[] body

    Set<String> userLikes = []
    long countLikes = 0L

    @DocumentReference(collection = "comments")
    Set<Comment> comments = []

    @CreatedDate
    LocalDateTime dateCreated

    @LastModifiedDate
    LocalDateTime lastModified

}
