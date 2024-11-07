package com.itiutiaiev.chipper.controller.api

import com.itiutiaiev.chipper.model.dto.CommentCreateDto
import com.itiutiaiev.chipper.model.dto.PostCreateDto
import com.itiutiaiev.chipper.model.dto.PostUpdateDto
import com.itiutiaiev.chipper.model.dto.UserDto
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated

@Validated
interface PostApi {


    @ApiResponses([
            @ApiResponse(code = 201, message = "Post created"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Bad Request")
    ])
    ResponseEntity<String> createPost(@Valid PostCreateDto userDto)

    @ApiResponses([
            @ApiResponse(code = 202, message = "Post updated"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Post not found")
    ])
    ResponseEntity<Void> updatePost(@Valid PostUpdateDto userUpdateDto)

    @ApiResponses([
            @ApiResponse(code = 204, message = "Post deleted"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Post not found")
    ])
    ResponseEntity<Void> deletePost(@NotBlank String postId)

    @ApiResponses([
            @ApiResponse(code = 202, message = "Subscribed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    ])
    ResponseEntity<Void> postLike(@NotBlank String postId)

    @ApiResponses([
            @ApiResponse(code = 202, message = "Subscribed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    ])
    ResponseEntity<Void> removeLike(@NotBlank String postId)

    @ApiResponses([
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    ])
    ResponseEntity<UserDto> getComments(@NotBlank String postId)

    @ApiResponses([
            @ApiResponse(code = 202, message = "Subscribed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    ])
    ResponseEntity<Void> addComment(@NotBlank String postId, @Valid CommentCreateDto commentCreateDto)

    @ApiResponses([
            @ApiResponse(code = 202, message = "Subscribed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    ])
    ResponseEntity<Void> removeComment(@NotBlank String postId, @NotBlank String commentId)

}
