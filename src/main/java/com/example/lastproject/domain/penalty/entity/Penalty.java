package com.example.lastproject.domain.penalty.entity;

import com.example.lastproject.common.Timestamped;
import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "penalty")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Penalty extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private Party partyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    public Penalty(Party partyId, User userId) {
        this.partyId = partyId;
        this.userId = userId;
    }

}
