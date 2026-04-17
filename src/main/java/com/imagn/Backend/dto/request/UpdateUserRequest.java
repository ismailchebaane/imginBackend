package com.imagn.Backend.dto.request;

import lombok.Data;


@Data

public class UpdateUserRequest {
    private String email;
    private String password;
    private String username;

}