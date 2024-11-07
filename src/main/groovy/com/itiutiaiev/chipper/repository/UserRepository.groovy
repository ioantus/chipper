package com.itiutiaiev.chipper.repository

import com.itiutiaiev.chipper.model.entity.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends MongoRepository<User, ObjectId> {

    Optional<User> findByEmailAndDeletedFalse(String email)

}