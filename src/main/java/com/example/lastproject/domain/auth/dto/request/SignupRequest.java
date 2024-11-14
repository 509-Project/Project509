package com.example.lastproject.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SignupRequest {

    @Email(message = "이메일 형식이 맞지 않습니다.")
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    private String email;

    @Pattern(regexp = "^(?=.*?[A-Za-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$",
            message = "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자 포함, 최소 8글자 이상")
    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    private String password;

    private String nickname;

    @NotBlank(message = "마켓 주소는 공백일 수 없습니다.")
    private String address;

    @NotNull(message = "위도는 공백일 수 없습니다.")
    private BigDecimal latitude;

    @NotNull(message = "경도는 공백일 수 없습니다.")
    private BigDecimal longitude;

    @NotBlank(message = "권한은 공백일 수 없습니다.")
    private String userRole;

}
