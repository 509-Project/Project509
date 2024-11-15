package com.example.lastproject.domain.party.repository;

import com.example.lastproject.domain.party.dto.response.NearbyPartyResponse;
import com.example.lastproject.domain.user.dto.NearbyBookmarkUserDto;

import java.math.BigDecimal;
import java.util.List;

public interface PartyQueryRepository {
    List<NearbyPartyResponse> getNearByParties(BigDecimal latitude, BigDecimal longitude);
    List<NearbyBookmarkUserDto> getUserIdWithDistanceNearbyParty(BigDecimal latitude, BigDecimal longitude, long itemId);
}
