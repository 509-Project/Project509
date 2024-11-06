package com.example.lastproject.domain.item.repository;

import com.example.lastproject.domain.item.dto.response.ItemResponse;
import com.example.lastproject.domain.item.dto.response.QItemResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.lastproject.domain.item.entity.QItem.item;

@Repository
@RequiredArgsConstructor
public class ItemQueryRepositoryImpl implements ItemQueryRepository {

    private final JPAQueryFactory q;

    @Override
    public List<ItemResponse> searchItemsByKeywordInCategory(String keyword) {

        List<ItemResponse> results = q
                .select(
                        new QItemResponse(item.id, item.category, item.productName)
                )
                .from(item)
                .where(item.category.like(keyword + "%").or(item.productName.like(keyword + "%")))
                .fetch();

        return results;
    }

}
