package com.example.lastproject.domain.likeitem.repository;

import com.example.lastproject.domain.likeitem.dto.response.LikeItemResponse;
import com.example.lastproject.domain.likeitem.dto.response.QLikeItemResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.lastproject.domain.likeitem.entity.QLikeItem.likeItem;

@Repository
@RequiredArgsConstructor
public class LikeItemQueryRepositoryImpl implements LikeItemQueryRepository {

    private final JPAQueryFactory q;

    @Override
    public List<LikeItemResponse> getBookmarkedItems(long bookmarkId) {
        List<LikeItemResponse> results = q
                .select(
                        new QLikeItemResponse(likeItem.item.id, likeItem.item.category, likeItem.item.productName)
                )
                .from(likeItem)
                .where(likeItem.user.id.eq(bookmarkId))
                .fetch();

        return results;
    }

    /**
     * 등록할 즐겨찾기 품목이 이미 등록된 품목인지 검증
     *
     * @param userId 즐겨찾기 등록할 유저아이디
     * @param itemId 즐겨찾기 등록할 품목아이디
     * @return 존재여부 반환
     */
    @Override
    public boolean existsByUserBookmarkedItem(long userId, long itemId) {
        boolean isPresent = q
                .selectOne()
                .from(likeItem)
                .where(likeItem.user.id.eq(userId).and(likeItem.item.id.eq(itemId)))
                .fetchFirst() != null;

        return isPresent;
    }

}
