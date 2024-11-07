package com.itiutiaiev.chipper.controller

import com.itiutiaiev.chipper.model.dto.ChangePasswordDto
import com.itiutiaiev.chipper.model.dto.LoginDto
import com.itiutiaiev.chipper.model.dto.UserDto
import com.itiutiaiev.chipper.model.dto.UserUpdateDto
import com.itiutiaiev.chipper.model.entity.User
import com.itiutiaiev.chipper.repository.UserRepository
import com.jayway.jsonpath.JsonPath
import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest

import java.time.LocalDate

@SpringBootTest
class UserControllerSpec extends BaseControllerSpec {

    @Resource
    private UserRepository userRepository

    def "test_getUser"() {
        when:" Unauthorised"
        def response = performGet("/api/v1/user/${firstTestUser.email}", null, null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" Email is invalid"
        String token = login(firstTestUser.email, standardPassword)
        response = performGet("/api/v1/user/invalidEmail", token, null)

        then:
        noExceptionThrown()
        response.status == 400
        response.contentType == 'application/json'
        response.contentAsString.contains("must be a well-formed email address")

        when:" Email is incorrect"
        response = performGet("/api/v1/user/incorrect@test.ua", token, null)

        then:
        noExceptionThrown()
        response.status == 404
        response.contentType == 'application/json'
        response.contentAsString.contains("Can't find user `incorrect@test.ua`")

        when:" Success"
        response = performGet("/api/v1/user/${firstTestUser.email}", token, null)
        def json = JsonPath.parse(response.contentAsString).json()

        then:
        noExceptionThrown()
        response.status == 200
        response.contentType == 'application/json'
        json['email'] == firstTestUser.email
        json['birthdate'] == firstTestUser.birthdate.toString()
        json['firstName'] == firstTestUser.firstName
        json['lastName'] == firstTestUser.lastName
        json['subscriptions'].empty
        json['subscribers'].empty
        !json['password']
    }

    def "test_login"() {
        when:" No data provided"
        def response = performPost("/api/v1/user/login", null, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" Email is incorrect"
        response = performPost("/api/v1/user/login", null,
                new LoginDto("incorrectEmail", "111"))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Password is too short"
        response = performPost("/api/v1/user/login", null,
                new LoginDto("invalid@test.ua", "000"))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Email is invalid"
        response = performPost("/api/v1/user/login", null,
                new LoginDto("invalid@test.ua", standardPassword))

        then:
        noExceptionThrown()
        response.status == 401

        when:" Password is invalid"
        response = performPost("/api/v1/user/login", null,
                new LoginDto(firstTestUser.email, "987654321"))

        then:
        noExceptionThrown()
        response.status == 401

        when:" Success"
        response = performPost("/api/v1/user/login", null,
                new LoginDto(firstTestUser.email, standardPassword))

        then:
        noExceptionThrown()
        response.status == 200
        response.contentType == 'application/json'
        response.contentAsString.contains("bearer")
    }

    def "test_register"() {
        when:" No data provided"
        def response = performPost("/api/v1/user/register", null, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" Email is incorrect"
        response = performPost("/api/v1/user/register", null,
                new UserDto(email: "incorrectEmail", password: "111"))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Password is too short"
        response = performPost("/api/v1/user/register", null,
                new UserDto(email: "newUser@test.ua", password: "000"))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Birthdate is mandatory"
        response = performPost("/api/v1/user/register", null,
                new UserDto(email: "newUser@test.ua", password: standardPassword))

        then:
        noExceptionThrown()
        response.status == 400

        when:" User already exists"
        response = performPost("/api/v1/user/register", null,
                new UserDto(email: firstTestUser.email, password: standardPassword, birthdate: LocalDate.of(2000, 1, 1)))

        then:
        noExceptionThrown()
        response.status == 400
        response.contentAsString == "User with email ${firstTestUser.email} already exists"

        when:" Success"
        response = performPost("/api/v1/user/register", null,
                new UserDto(email: "newUser@test.ua", password: standardPassword,
                        birthdate: LocalDate.of(2000, 1, 1)))
        User newUser = userRepository.findByEmailAndDeletedFalse("newUser@test.ua").orElse(null)

        then:
        noExceptionThrown()
        response.status == 201
        newUser?.email == "newUser@test.ua"
        newUser?.birthdate == LocalDate.of(2000, 1, 1)

        cleanup:
        userService.delete("newUser@test.ua")
    }

    def "test_update"() {
        when:" Unauthorised"
        def response = performPut("/api/v1/user/update", null,
                new UserUpdateDto(password: standardPassword, birthdate: LocalDate.of(2000, 1, 1)))

        then:
        noExceptionThrown()
        response.status == 401

        when:" No data provided"
        String token = login(firstTestUser.email, standardPassword)
        response = performPut("/api/v1/user/update", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" No password"
        response = performPut("/api/v1/user/update", token,
                new UserUpdateDto(birthdate: LocalDate.of(2000, 1, 1)))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Password is too short"
        response = performPut("/api/v1/user/update", token,
                new UserUpdateDto(password: "1", birthdate: LocalDate.of(2000, 1, 1)))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Birthdate is mandatory"
        response = performPut("/api/v1/user/update", token, new UserUpdateDto(password: standardPassword))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Success"
        response = performPut("/api/v1/user/update", token,
                new UserUpdateDto(password: standardPassword, firstName: "newFirstName", lastName: "newLastName",
                birthdate: LocalDate.of(2000, 1, 1)))
        firstTestUser = userRepository.findByEmailAndDeletedFalse(firstTestUser.email).orElse(null)

        then:
        noExceptionThrown()
        response.status == 202
        firstTestUser?.firstName == "newFirstName"
        firstTestUser?.lastName == "newLastName"
        firstTestUser?.birthdate == LocalDate.of(2000, 1, 1)
    }

    def "test_changePassword"() {
        when:" Unauthorised"
        def response = performPatch("/api/v1/user/changePassword", null,
                new ChangePasswordDto(standardPassword))

        then:
        noExceptionThrown()
        response.status == 401

        when:" No data provided"
        String token = login(firstTestUser.email, standardPassword)
        response = performPatch("/api/v1/user/changePassword", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" Password is too short"
        response = performPatch("/api/v1/user/changePassword", token, new ChangePasswordDto("1"))

        then:
        noExceptionThrown()
        response.status == 400

        when:" Success"
        response = performPatch("/api/v1/user/changePassword", token, new ChangePasswordDto("987654321"))
        firstTestUser = userRepository.findByEmailAndDeletedFalse(firstTestUser.email).orElse(null)

        then:
        noExceptionThrown()
        response.status == 202
        passwordEncoder.matches("987654321", firstTestUser?.password)
    }

    def "test_subscribe"() {
        when:" Unauthorised"
        def response = performPatch("/api/v1/user/subscribe/${firstTestUser.email}", null, null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" Email is incorrect"
        String token = login(firstTestUser.email, standardPassword)
        response = performPatch("/api/v1/user/subscribe/incorrect", token, null)

        then:
        noExceptionThrown()
        response.status == 400

        when:" User is not exists"
        response = performPatch("/api/v1/user/subscribe/notExists@test.ua", token, null)

        then:
        noExceptionThrown()
        response.status == 404
        response.contentAsString == "Can't find user `notExists@test.ua`"

        when:" Success"
        response = performPatch("/api/v1/user/subscribe/${secondTestUser.email}", token, null)
        firstTestUser = userRepository.findByEmailAndDeletedFalse(firstTestUser.email).orElse(null)
        secondTestUser = userRepository.findByEmailAndDeletedFalse(secondTestUser.email).orElse(null)

        then:
        noExceptionThrown()
        response.status == 202
        firstTestUser.subscriptions.contains(secondTestUser.email)
        secondTestUser.subscribers.contains(firstTestUser.email)

        when:" Trying to subscribe again"
        response = performPatch("/api/v1/user/subscribe/${secondTestUser.email}", token, null)

        then:
        noExceptionThrown()
        response.status == 400
        response.contentAsString == "Can't subscribe, because user [${firstTestUser.email}] is not subscribed to [${secondTestUser.email}]"
    }


    def "test_delete"() {
        when:" Unauthorised"
        def response = performDelete("/api/v1/user/delete", null)

        then:
        noExceptionThrown()
        response.status == 401

        when:" Success"
        String token = login(firstTestUser.email, standardPassword)
        response = performDelete("/api/v1/user/delete", token)
        firstTestUser = userRepository.findByEmailAndDeletedFalse(firstTestUser.email).orElse(null)

        then:
        noExceptionThrown()
        response.status == 204
        !firstTestUser

        cleanup:
        // Need to create user back in order to avoid data leakage
        def testUserDto = new UserDto(
                email: 'test@test.ua',
                password: passwordEncoder.encode(standardPassword),
                birthdate: LocalDate.of(1984, 11, 11)
        )
        firstTestUser = userService.create(testUserDto)
    }

}