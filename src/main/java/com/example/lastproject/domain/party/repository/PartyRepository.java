package com.example.lastproject.domain.party.repository;

import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.party.enums.PartyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long> {

    List<Party> findAllByCreatorId(Long creatorId);
    List<Party> findAllByPartyStatus(PartyStatus partyStatus);

    Optional<Party> findByIdAndCreatorId(Long partyId, Long creatorId);
    Optional<Party> findByIdAndPartyStatus(Long partyId, PartyStatus partyStatus);

}
