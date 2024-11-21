package com.example.lastproject.domain.address.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMarket is a Querydsl query type for Market
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMarket extends EntityPathBase<Market> {

    private static final long serialVersionUID = 1355549411L;

    public static final QMarket market = new QMarket("market");

    public final StringPath address = createString("address");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath marketName = createString("marketName");

    public QMarket(String variable) {
        super(Market.class, forVariable(variable));
    }

    public QMarket(Path<? extends Market> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMarket(PathMetadata metadata) {
        super(Market.class, metadata);
    }

}

