package de.ronnywalter.eve.frontend.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EveUserDetails {

    private String name;
    private int id;

    private String accessToken;
    private String refreshToken;

    private int expiresIn;

}
