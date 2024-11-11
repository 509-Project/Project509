package com.example.lastproject.domain.item.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item"
        , indexes = {
        @Index(name = "category_index", columnList = "category"),
        @Index(name = "product_index", columnList = "product_name")}
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private String productName;

    public Item(String category, String productName) {
        this.category = category;
        this.productName = productName;
    }

}
