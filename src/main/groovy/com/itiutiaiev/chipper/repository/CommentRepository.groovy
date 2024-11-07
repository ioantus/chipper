package com.itiutiaiev.chipper.repository

import com.itiutiaiev.chipper.model.entity.Comment
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface CommentRepository extends MongoRepository<Comment, ObjectId> {

}