package com.example.lastproject.domain.party.repository;

import com.example.lastproject.domain.party.dto.response.NearbyPartyResponse;
import com.example.lastproject.domain.party.dto.response.QNearbyPartyResponse;
import com.example.lastproject.domain.user.dto.NearbyBookmarkUserDto;
import com.example.lastproject.domain.user.dto.QNearbyBookmarkUserDto;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static com.example.lastproject.domain.likeitem.entity.QLikeItem.likeItem;
import static com.example.lastproject.domain.party.entity.QParty.party;
import static com.example.lastproject.domain.user.entity.QUser.user;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

@Repository
@RequiredArgsConstructor
public class PartyQueryRepositoryImpl implements PartyQueryRepository {

    private final JPAQueryFactory q;

    public List<NearbyPartyResponse> getNearByParties(BigDecimal latitude, BigDecimal longitude) {

        // 거리 계산을 위한 하버사인 공식
        NumberTemplate<BigDecimal> distance = numberTemplate(BigDecimal.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1})))",
                latitude, party.latitude, party.longitude, longitude);

        List<NearbyPartyResponse> results = q
                .select(
                        new QNearbyPartyResponse(party.id, party.marketName, party.marketAddress, distance, party.item.id)
                )
                .from(party)
                .where(distance.loe(10))  // 10KM 이하의 거리 필터
                .orderBy(distance.asc())           // 거리 순으로 정렬
                .fetch();

        return results;
    }

    // 파티 생성시 반경 10 km 내에 지정위치를 등록한 유저들중 즐겨찾기품목과 파티의 장볼품목이 동일한 유저들의 ID 값과 생성된 파티와의 거리를 반환
    public List<NearbyBookmarkUserDto> getUserIdWithDistanceNearbyParty(BigDecimal latitude, BigDecimal longitude, long itemId) {

        // 거리 계산을 위한 하버사인 공식
        NumberTemplate<BigDecimal> distance = numberTemplate(BigDecimal.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1})))",
                latitude, user.latitude, user.longitude, longitude);

        List<NearbyBookmarkUserDto> results = q
                .select(new QNearbyBookmarkUserDto(user.id, distance))
                .from(user)
                .leftJoin(user.likeItems, likeItem).fetchJoin()
                .where(distance.loe(10)) // 10KM 이하의 거리 필터
                .where(likeItem.item.id.eq(itemId)) // 파티의 품목과 유저의 즐겨찾기 품목이 같은지 필터
                .fetch();

        return results;
    }

}
