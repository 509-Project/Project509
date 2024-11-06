package com.example.lastproject.domain.auth.dto.response;

import com.example.lastproject.common.enums.CustomMessage;
import lombok.Getter;

@Getter
public class SigninResponse {

    private final String msg;

    public SigninResponse() {
        this.msg = CustomMessage.SIGNIN_SUCCESS.getMessage();
    }

}
