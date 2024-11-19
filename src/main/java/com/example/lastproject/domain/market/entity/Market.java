package com.example.lastproject.domain.market.entity;

import com.example.lastproject.domain.party.entity.Party;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "market_name", nullable = false)
    private String marketName;

    @Column(name = "market_address", nullable = false)
    private String marketAddress;

    @Column(name = "latitude", nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false)
    private BigDecimal longitude;

    public Market(Party party) {
        this.marketName = party.getMarketName();
        this.marketAddress = party.getMarketAddress();
        this.latitude = party.getLatitude();
        this.longitude = party.getLongitude();
    }

}
