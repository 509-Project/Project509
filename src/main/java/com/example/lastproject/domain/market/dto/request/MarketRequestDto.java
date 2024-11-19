package com.example.lastproject.domain.market.dto.request;

import com.example.lastproject.domain.market.entity.Market;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

//@Getter
//public class MarketRequestDto {
//
//    @NotBlank(message = "마켓 이름은 필수 입력 항목입니다.")
//    private String marketName;
//
//    @NotBlank(message = "주소는 필수 입력 항목입니다.")
//    private String address;
//
//    @NotBlank(message = "위도는 필수 입력 항목입니다.")
//    private String latitude;
//
//    @NotBlank(message = "경도는 필수 입력 항목입니다.")
//    private String longitude;
//
//
//    public Market toEntity() {
//        return new Market(this.marketName, this.);
//    }
//
//}
