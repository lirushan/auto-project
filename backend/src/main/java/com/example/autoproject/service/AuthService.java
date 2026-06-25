package com.example.autoproject.service;

import com.example.autoproject.vo.LoginVO;

public interface AuthService {
    LoginVO login(String username, String password);
}
