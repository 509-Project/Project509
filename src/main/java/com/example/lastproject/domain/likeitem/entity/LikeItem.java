package com.example.lastproject.domain.likeitem.entity;

import com.example.lastproject.domain.item.entity.Item;
import com.example.lastproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "like_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public LikeItem(User user, Item item) {
        this.user = user;
        this.item = item;
    }

}
