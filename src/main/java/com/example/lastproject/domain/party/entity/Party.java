package com.example.lastproject.domain.party.entity;

import com.example.lastproject.common.Timestamped;
import com.example.lastproject.domain.item.entity.Item;
//import com.example.lastproject.domain.market.entity.Market;
import com.example.lastproject.domain.party.enums.PartyStatus;
import com.example.lastproject.domain.partymember.entity.PartyMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "Party")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Party extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "market_name", nullable = false)
    private String marketName;

    @Column(name = "market_address", nullable = false)
    private String marketAddress;

    @Column(name = "latitude", nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false)
    private BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "item_count", nullable = false)
    private int itemCount;

    @Column(name = "item_unit", nullable = false)
    private String itemUnit;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(name = "members_count", nullable = false)
    private int membersCount;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "market_id")
//    private Market market;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyStatus partyStatus = PartyStatus.OPEN;

    @OneToMany(mappedBy = "party")
    private List<PartyMember> partyMembers;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    public Party(String marketName, String marketAddress, BigDecimal latitude, BigDecimal longitude,
                 Item item, int itemCount, String itemUnit, String startTime, String endTime,
                 int membersCount, Long creatorId) {
        this.marketName = marketName;
        this.marketAddress = marketAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.item = item;
        this.itemCount = itemCount;
        this.itemUnit = itemUnit;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.startTime = LocalDateTime.parse(startTime, formatter);
        this.endTime = LocalDateTime.parse(endTime, formatter);
        this.membersCount = membersCount;
        this.partyStatus = PartyStatus.OPEN;
        this.creatorId = creatorId;
    }

    // 장보기 완료
    public void completeParty() {
        this.partyStatus = PartyStatus.DONE;
    }

    // 파티 취소
    public void cancelParty() {
        this.partyStatus = PartyStatus.CANCELED;
    }

    // 파티 생성자 확인
    public boolean isCreator(Long userId) {
        return this.creatorId.equals(userId);
    }

    // 파티 상태 업데이트
    public void updateStatus(PartyStatus newStatus) {
        this.partyStatus = newStatus;
    }

    public PartyStatus getStatus() {
        return this.partyStatus;
    }

    // 상세 정보 업데이트 및 시간 검증 로직 추가
    public void updateDetails(Item item, int itemCount, String itemUnit, LocalDateTime startTime, LocalDateTime endTime, int membersCount) {
        this.item = item;
        this.itemCount = itemCount;
        this.itemUnit = itemUnit;
        this.startTime = startTime;
        this.endTime = endTime;
        this.membersCount = membersCount;
    }

}
