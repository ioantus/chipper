package com.itiutiaiev.chipper.model.entity

import groovy.transform.Canonical
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

import java.time.LocalDateTime

@Document(collection = "comments")
@Canonical
class Comment {

    @MongoId
    ObjectId id

    @Indexed
    @NotBlank(message = "Author email is mandatory")
    String authorEmail

    @NotBlank(message = "Text is mandatory")
    @Size(max = 500, message = "Text must not exceed 500 characters")
    String text

    @CreatedDate
    LocalDateTime dateCreated

}
