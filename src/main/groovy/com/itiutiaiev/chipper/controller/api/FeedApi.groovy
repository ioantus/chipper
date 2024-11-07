package com.itiutiaiev.chipper.controller.api

import com.itiutiaiev.chipper.model.dto.PostDto
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import jakarta.validation.constraints.Email
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated

@Validated
interface FeedApi {

    @ApiResponses([
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized")
    ])
    ResponseEntity<List<PostDto>> getMyFeed()

    @ApiResponses([
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    ])
    ResponseEntity<List<PostDto>> getUserFeed(@Email String email)

}
