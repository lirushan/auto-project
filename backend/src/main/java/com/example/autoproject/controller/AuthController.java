package com.example.autoproject.controller;

import com.example.autoproject.common.Result;
import com.example.autoproject.dto.LoginRequest;
import com.example.autoproject.service.AuthService;
import com.example.autoproject.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request.username(), request.password()));
    }
}
