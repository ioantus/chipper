package com.itiutiaiev.chipper.controller

import com.itiutiaiev.chipper.controller.api.UserApi
import com.itiutiaiev.chipper.model.dto.ChangePasswordDto
import com.itiutiaiev.chipper.model.dto.LoginDto
import com.itiutiaiev.chipper.model.dto.UserDto
import com.itiutiaiev.chipper.model.dto.UserUpdateDto
import com.itiutiaiev.chipper.service.impl.AuthServiceImpl
import com.itiutiaiev.chipper.service.impl.UserServiceImpl
import jakarta.annotation.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

@Validated
@RestController
@RequestMapping("/api/v1/user")
class UserController implements UserApi {

    @Resource
    private UserServiceImpl userService
    @Resource
    private AuthServiceImpl authService
    @Resource
    PasswordEncoder passwordEncoder

    @GetMapping("{email}")
    ResponseEntity<UserDto> getUser(@PathVariable("email") String email) {
        ResponseEntity.ok(userService.toDto(userService.findByEmail(email)))
    }

    @PostMapping("/login")
    ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        ResponseEntity.ok([bearer: authService.authenticate(loginDto)])
    }

    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody UserDto userDto) {
        userDto.password = passwordEncoder.encode(userDto.password)
        userService.create(userDto)
        ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PutMapping("/update")
    ResponseEntity<Void> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        userUpdateDto.password = passwordEncoder.encode(userUpdateDto.password)
        userService.update(userUpdateDto)
        ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

    @PatchMapping("/changePassword")
    ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(passwordEncoder.encode(changePasswordDto.password))
        ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

    @PatchMapping("/subscribe/{email}")
    ResponseEntity<Void> subscribe(@PathVariable("email") String email) {
        userService.subscribe(email)
        ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

    @PatchMapping("/unsubscribe/{email}")
    ResponseEntity<Void> unsubscribe(@PathVariable("email") String email) {
        userService.unsubscribe(email)
        ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteUser(Principal principal) {
        userService.delete(principal.name)
        ResponseEntity.noContent().build()
    }

}
