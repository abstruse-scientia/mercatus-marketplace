package com.scientia.mercatus.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class VerificationServiceUtil {

    @Value("${frontend-url:http://localhost:5173}")
    private String frontendBaseURL;

    public String generateVerificationLink(String accessToken) {
        return UriComponentsBuilder.fromUriString(frontendBaseURL)
                .path("reset-password")
                .queryParam("token", accessToken)
                .build()
                .toUriString();
    }
}
