package com.itiutiaiev.chipper.controller.api

import com.itiutiaiev.chipper.model.dto.ChangePasswordDto
import com.itiutiaiev.chipper.model.dto.LoginDto
import com.itiutiaiev.chipper.model.dto.UserDto
import com.itiutiaiev.chipper.model.dto.UserUpdateDto
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated

import java.security.Principal

@Validated
interface UserApi {

    @ApiResponses([
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    ])
    ResponseEntity<UserDto> getUser(@Email String email)

    @ApiResponses([
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "User not found")
    ])
    ResponseEntity<Map<String, String>> login(@Valid LoginDto loginDto)

    @ApiResponses([
            @ApiResponse(code = 201, message = "User registered"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Bad Request")
    ])
    ResponseEntity<Void> register(@Valid UserDto userDto)

    @ApiResponses([
            @ApiResponse(code = 202, message = "User updated"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    ])
    ResponseEntity<Void> updateUser(@Valid UserUpdateDto userUpdateDto)

    @ApiResponses([
            @ApiResponse(code = 202, message = "Password changed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    ])
    ResponseEntity<Void> changePassword(@Valid ChangePasswordDto changePasswordDto)

    @ApiResponses([
            @ApiResponse(code = 202, message = "Subscribed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    ])
    ResponseEntity<Void> subscribe(@Email String email)

    @ApiResponses([
            @ApiResponse(code = 202, message = "Unsubscribed"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    ])
    ResponseEntity<Void> unsubscribe(@Email String email)

    @ApiResponses([
            @ApiResponse(code = 204, message = "User deleted"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "User not found")
    ])
    ResponseEntity<Void> deleteUser(Principal principal)

}
