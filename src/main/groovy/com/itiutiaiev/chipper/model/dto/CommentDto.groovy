package com.itiutiaiev.chipper.model.dto

import groovy.transform.Canonical

import java.time.LocalDateTime

@Canonical
class CommentDto {

    String authorEmail
    String text
    LocalDateTime dateCreated

}
