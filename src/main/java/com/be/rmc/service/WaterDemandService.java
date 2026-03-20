package com.be.rmc.service;

import com.be.rmc.dto.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public interface WaterDemandService {

    ApiResponse processWaterDemand(String encryptedData) throws Exception;
}
