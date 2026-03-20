package com.be.rmc.controller;

import com.be.rmc.dto.ApiResponse;
import com.be.rmc.service.UserInfoService;
import com.be.rmc.service.WaterDemandService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@Slf4j
public class RmcController {

    private final WaterDemandService waterDemandService;
    private final UserInfoService userInfoService;

    public RmcController(WaterDemandService waterDemandService, UserInfoService userInfoService) {
        this.waterDemandService = waterDemandService;
        this.userInfoService = userInfoService;
    }

    @PostMapping("/water-demand")
    public ResponseEntity<ApiResponse> payWaterDemand(@RequestParam String encryptedData, HttpServletRequest request) {

        String taskName = "pay_water_demand";
        log.info("{} :: started", taskName);

        try {

            if(encryptedData == null || encryptedData.isEmpty()) {
                ApiResponse apiResponse = new ApiResponse(
                        "failure",
                        HttpStatus.BAD_REQUEST.value(),
                        "Missing required parameter: encryptedData",
                        new HashMap<>()
                );

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(apiResponse);
            }

            ApiResponse response = waterDemandService.processWaterDemand(encryptedData);

            log.info("api response for get water demand :: {}", response);

            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response);

        } catch (Exception e) {

            log.error("{} :: error :: {}", taskName, e.getMessage(), e);

            ApiResponse apiResponse = new ApiResponse(
                    "failure",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    new HashMap<>()
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(apiResponse);
        }
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "userId", required = false) String userId) {
        if (userId != null && userInfoService.validateRegisteredUser(userId)) {
            return "index";
        } else {
            return "unauthorized";
        }
    }
}
