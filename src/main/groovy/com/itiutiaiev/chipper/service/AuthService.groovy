package com.itiutiaiev.chipper.service

import com.itiutiaiev.chipper.model.dto.LoginDto

interface AuthService {

    String authenticate(LoginDto loginDto)

}