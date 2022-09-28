package de.ronnywalter.eve.frontend.security;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserDTO {

    private int id;
    private String name;
    private Instant lastLogin;

}
