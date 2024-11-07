package com.itiutiaiev.chipper.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.itiutiaiev.chipper.model.dto.LoginDto
import com.itiutiaiev.chipper.model.dto.UserDto
import com.itiutiaiev.chipper.model.entity.User
import com.itiutiaiev.chipper.service.impl.UserServiceImpl
import com.jayway.jsonpath.JsonPath
import jakarta.annotation.Resource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

@AutoConfigureMockMvc
class BaseControllerSpec extends Specification {

    final String standardPassword = '123456789'

    @Resource
    private MockMvc mockMvc
    @Resource
    private ObjectMapper objectMapper
    @Resource
    protected UserServiceImpl userService
    @Resource
    protected PasswordEncoder passwordEncoder

    @Shared
    protected User firstTestUser, secondTestUser

    @BeforeEach
    void setup() {
        def testUserDto = new UserDto(
                email: 'test@test.ua',
                password: passwordEncoder.encode(standardPassword),
                firstName: "test",
                lastName: "test",
                birthdate: LocalDate.of(1984, 11, 11)
        )
        firstTestUser = userService.create(testUserDto)
        testUserDto.email = "secondTest@test.ua"
        secondTestUser = userService.create(testUserDto)
    }

    @AfterEach
    void cleanup() {
        userService.delete(firstTestUser.email)
        userService.delete(secondTestUser.email)
    }

    protected String login(String email, String password) {
        JsonPath.parse(
                performPost("/api/v1/user/login", null, new LoginDto(email: email, password: password))
                    .contentAsString
        ).json()['bearer']
    }

    protected MockHttpServletResponse performGet(String url, String token, def body) {
        mockMvc.perform(get(url)
                .header("Authorization", token ? "bearer $token" : "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .response
    }

    protected MockHttpServletResponse performPost(String url, String token, def body) {
        mockMvc.perform(post(url)
                .header("Authorization", token ? "bearer $token" : "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .response
    }

    protected MockHttpServletResponse performPut(String url, String token, def body) {
        mockMvc.perform(put(url)
                .header("Authorization", token ? "bearer $token" : "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .response
    }

    protected MockHttpServletResponse performPatch(String url, String token, def body) {
        mockMvc.perform(patch(url)
                .header("Authorization", token ? "bearer $token" : "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .response
    }

    protected MockHttpServletResponse performDelete(String url, String token) {
        mockMvc.perform(delete(url)
                .header("Authorization", "bearer ${token ?: ''}")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .response
    }

}