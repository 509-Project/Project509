package com.example.lastproject.domain.partymember.dto.response;

import com.example.lastproject.domain.partymember.entity.PartyMember;
import com.example.lastproject.domain.partymember.enums.PartyMemberInviteStatus;
import com.example.lastproject.domain.partymember.enums.PartyMemberRole;
import lombok.Getter;

@Getter
public class PartyMemberResponse {

    private Long id; // 파티 멤버 ID
    private PartyMemberInviteStatus inviteStatus; // 초대 상태
    private PartyMemberRole role; // 역할

    public PartyMemberResponse(PartyMember partyMember) {
        this.id = partyMember.getId();
        this.inviteStatus = partyMember.getInviteStatus();
        this.role = partyMember.getRole();
    }
}
