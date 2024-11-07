package com.itiutiaiev.chipper.repository

import com.itiutiaiev.chipper.model.entity.Post
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.MongoRepository

interface PostRepository extends MongoRepository<Post, ObjectId> {

    Optional<List<Post>> findAllByAuthorEmailIn(List<String> emails, Sort sort)

}