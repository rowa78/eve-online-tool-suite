package de.ronnywalter.eve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiTokenDTO {

    private int characterId;

    private String accessToken;
    private String refreshToken;

    private ZonedDateTime expiryDate;
    private String clientId;
    private String clientSecret;
}
