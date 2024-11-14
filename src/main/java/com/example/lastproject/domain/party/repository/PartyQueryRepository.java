package com.example.lastproject.domain.party.repository;

import com.example.lastproject.domain.party.dto.response.NearByPartyResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PartyQueryRepository {
    List<NearByPartyResponse> getNearByParties(BigDecimal latitude, BigDecimal longitude);
}
