package com.itiutiaiev.chipper.model.dto

import com.itiutiaiev.chipper.model.entity.Comment
import groovy.transform.Canonical

import java.time.LocalDateTime

@Canonical
class PostDto extends PostUpdateDto {

    String authorEmail

    long countLikes

    Set<Comment> comments

    LocalDateTime dateCreated

}
