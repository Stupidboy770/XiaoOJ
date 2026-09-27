package org.example.demooj.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.example.demooj.dto.UserDto;
import org.example.demooj.service.LoginValidation;
import org.example.demooj.service.impl.LoginValidationImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    @Resource
    LoginValidation loginValidationImpl;

    @PostMapping("/login")
    public ResponseEntity<String> userValidation(@RequestBody UserDto userDto) {
        String token=loginValidationImpl.loginUser(userDto.getUserId(), userDto.getPassword());
        if(token!=null){
            if(loginValidationImpl.isAdmin(userDto.getUserId()))return new ResponseEntity<>(token,HttpStatus.ACCEPTED);
            return new ResponseEntity<>(token, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
