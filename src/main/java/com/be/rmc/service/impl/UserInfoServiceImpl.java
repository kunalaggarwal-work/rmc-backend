package com.be.rmc.service.impl;

import com.be.rmc.dto.User;
import com.be.rmc.entity.ConsumerTransactionDetails;
import com.be.rmc.feign.RmcFeignInterface;
import com.be.rmc.repository.ConsumerTransactionDetailsRepository;
import com.be.rmc.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    private final RmcFeignInterface rmcFeignInterface;

    private final ConsumerTransactionDetailsRepository consumerTransactionDetailsRepository;

    public UserInfoServiceImpl(RmcFeignInterface rmcFeignInterface, ConsumerTransactionDetailsRepository consumerTransactionDetailsRepository) {
        this.rmcFeignInterface = rmcFeignInterface;
        this.consumerTransactionDetailsRepository = consumerTransactionDetailsRepository;
    }

    @Override
    public boolean validateRegisteredUser(String userId) {

        log.info("Going to call - validateUser api  function defined in whatsapp template service to validate user with userId -{} ", userId);

        User user = rmcFeignInterface.validateUser(userId).getBody();

        log.info("user details received from whatsapp template service for user with userId -{} is -{} ", userId, user);

        if(user == null) return false;

        // create DB entry for user if validated
        log.info("User validated successfully, thus going to create DB entry for user with userId -{} ", userId);

        consumerTransactionDetailsRepository.save(ConsumerTransactionDetails.builder()
                        .phoneNumber(user.getMobileNumber())
                        .userEmail("TestUser@gmail.com")
                        .userName(user.getName())
                        .accountId("RANWC38247384141")
                .build());

        log.info("User details saved successfully in DB for user with userId -{} ", userId);

        return true;
    }
}
