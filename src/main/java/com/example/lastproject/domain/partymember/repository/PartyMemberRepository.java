package com.example.lastproject.domain.partymember.repository;

import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.party.enums.PartyStatus;
import com.example.lastproject.domain.partymember.entity.PartyMember;
import com.example.lastproject.domain.partymember.enums.PartyMemberInviteStatus;
import com.example.lastproject.domain.partymember.enums.PartyMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {

    List<PartyMember> findByInviteStatus(PartyMemberInviteStatus status);
    List<PartyMember> findByPartyId(Long partyId);
    List<PartyMember> findByUserId(Long userId);
    Optional<PartyMember> findByPartyIdAndUserIdAndRoleNotAndInviteStatusNot(Long partyId, Long userId, PartyMemberRole role, PartyMemberInviteStatus inviteStatus);
    List<Party> findAllByStatus(PartyStatus status);

    Optional<PartyMember> findByPartyIdAndUserId(Long partyId, Long userId);
    Optional<PartyMember> findByPartyIdAndUserIdAndRole(Long partyId, Long userId, PartyMemberRole role);

}
