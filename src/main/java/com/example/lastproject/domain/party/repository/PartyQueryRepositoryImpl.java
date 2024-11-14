package com.example.lastproject.domain.party.repository;

import com.example.lastproject.domain.party.dto.response.NearByPartyResponse;
import com.example.lastproject.domain.party.dto.response.QNearByPartyResponse;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static com.example.lastproject.domain.party.entity.QParty.party;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

@Repository
@RequiredArgsConstructor
public class PartyQueryRepositoryImpl implements PartyQueryRepository {

    private final JPAQueryFactory q;

    public List<NearByPartyResponse> getNearByParties(BigDecimal latitude, BigDecimal longitude) {

        // 거리 계산을 위한 하버사인 공식
        NumberTemplate<BigDecimal> distance = numberTemplate(BigDecimal.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1})))",
                latitude, party.latitude, party.longitude, longitude);

        List<NearByPartyResponse> results = q
                .select(
                        new QNearByPartyResponse(party.id, party.marketName, party.marketAddress, distance, party.item.id)
                )
                .from(party)
                .where(distance.loe(10))  // 10KM 이하의 거리 필터
                .orderBy(distance.asc())           // 거리 순으로 정렬
                .fetch();

        return results;
    }

}
