package com.be.rmc.service;

import org.springframework.stereotype.Service;

@Service
public interface UserInfoService {

    boolean validateRegisteredUser(String userId);
}
