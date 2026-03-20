package com.be.rmc.service.impl;

import com.be.rmc.dto.ApiResponse;
import com.be.rmc.dto.PayWaterDemandDTO;
import com.be.rmc.entity.ConsumerTransactionDetails;
import com.be.rmc.repository.ConsumerTransactionDetailsRepository;
import com.be.rmc.service.WaterDemandService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WaterDemandServiceImpl implements WaterDemandService {

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    private final ConsumerTransactionDetailsRepository consumerTransactionDetailsRepository;

    @Value("${rmc-props.en-aes-key}")
    private String encryptedAesKey;

    @Value("${rmc-props.encrypt-response}")
    private String isEncryptResponse;

    public WaterDemandServiceImpl(ObjectMapper objectMapper, RestTemplate restTemplate, ConsumerTransactionDetailsRepository consumerTransactionDetailsRepository) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.consumerTransactionDetailsRepository = consumerTransactionDetailsRepository;
    }


    @Override
    public ApiResponse processWaterDemand(String encryptedData) throws Exception {

        boolean encryptResponse = isEncryptResponse.equalsIgnoreCase("true");
        String decryptedData = null;

        if (encryptedData != null && !encryptedData.isEmpty()) {
            decryptedData = decryptAES128ECB(encryptedData, encryptedAesKey);
        }

        PayWaterDemandDTO payWaterDemandDTO = objectMapper.readValue(decryptedData, PayWaterDemandDTO.class);

        log.info("Pay Water Demand DTO: {}", payWaterDemandDTO);

        try {
            ResponseEntity<Map<String, Object>> response = payWaterDemand(payWaterDemandDTO);

            log.info("Response from Water Demand API: {}", response);

            if (response == null) {
                return buildFailure("There seems to be some technical issue.", HttpStatus.BAD_REQUEST);
            }

            int status = response.getStatusCode().value();

            if (status == HttpStatus.OK.value()) {

                Map<String, Object> data = response.getBody();

                ApiResponse apiResponse = new ApiResponse(
                        "success",
                        HttpStatus.OK.value(),
                        "Water details fetch successfully.",
                        data
                );

                if(encryptResponse){
                    String apiResponseJson = objectMapper.writeValueAsString(apiResponse);
                    String encryptedApiResponse = encryptAES128ECB(apiResponseJson, encryptedAesKey);
                    return new ApiResponse(
                            "success",
                            HttpStatus.OK.value(),
                            "Water details fetch successfully.",
                            Map.of("data", encryptedApiResponse)
                    );
                }

                log.info("Going to save water demand details in DB for accountId: {}", payWaterDemandDTO.getAccountId());

                // TODO : update DB entry

                return apiResponse;
            }

            if (status == HttpStatus.NOT_FOUND.value()) {

                String errorMsg = "Resource not found.";

                ApiResponse apiResponse = buildFailure(errorMsg, HttpStatus.NOT_FOUND);

                if(encryptResponse){
                    String apiResponseJson = objectMapper.writeValueAsString(apiResponse);
                    String encryptedApiResponse = encryptAES128ECB(apiResponseJson, encryptedAesKey);
                    return new ApiResponse(
                            "failure",
                            HttpStatus.NOT_FOUND.value(),
                            errorMsg,
                            Map.of("data", encryptedApiResponse)
                    );
                }

                return apiResponse;
            }

            if (status >= 400 && status <= 500) {

                String errorMsg = "Bad request";

                ApiResponse apiResponse = buildFailure(errorMsg, HttpStatus.BAD_REQUEST);

                if(encryptResponse){
                    String apiResponseJson = objectMapper.writeValueAsString(apiResponse);
                    String encryptedApiResponse = encryptAES128ECB(apiResponseJson, encryptedAesKey);
                    return new ApiResponse(
                            "failure",
                            HttpStatus.BAD_REQUEST.value(),
                            errorMsg,
                            Map.of("data", encryptedApiResponse)
                    );
                }

                return apiResponse;
            }
        }
        catch (HttpClientErrorException ex) {

            int statusCode = ex.getStatusCode().value();
            String errorBody = ex.getResponseBodyAsString();

            log.info("Error while fetching Water Demand Details: {} and status code is {}", errorBody, statusCode);

        }
        catch (HttpServerErrorException ex) {

            int statusCode = ex.getStatusCode().value();
            log.info("Error while fetching Water Demand Details: {}", statusCode);
        }


        return buildFailure("There seems to be some technical issue.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<Map<String, Object>> payWaterDemand(PayWaterDemandDTO data) {

        log.info("Initiating water demand fetch for accountId: {}", data.getAccountId());
        String endpoint = "https://municipalservices.jharkhand.gov.in/AmurtMisPortal/getWaterDemand";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("consumer_no", data.getAccountId());

        try {
            log.info("Payload: {}", objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        return restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
    }

    private ApiResponse buildFailure(String message, HttpStatus status) {

        return new ApiResponse(
                "failure",
                status.value(),
                message,
                new HashMap<>()
        );
    }


    public static String decryptAES128ECB(String encryptedData, String aesKey) throws Exception {

        log.info("Decrypting data with AES-128 ECB mode -> encryptedData is {} and aesKey is {}", encryptedData, aesKey);
        byte[] keyBytes = aesKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static String encryptAES128ECB(String plainText, String aesKey) throws Exception {

        byte[] keyBytes = aesKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }



}
