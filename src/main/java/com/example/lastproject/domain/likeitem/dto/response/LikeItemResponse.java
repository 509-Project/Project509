package com.example.lastproject.domain.likeitem.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class LikeItemResponse {

    private final long id;
    private final String category;
    private final String productName;

    @QueryProjection
    public LikeItemResponse(long id, String category, String productName) {
        this.id = id;
        this.category = category;
        this.productName = productName;
    }

}
