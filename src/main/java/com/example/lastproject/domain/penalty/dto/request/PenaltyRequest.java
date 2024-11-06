package com.example.lastproject.domain.penalty.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PenaltyRequest {

    private List<Long> userIds;

    public PenaltyRequest(List<Long> userIds) {
        this.userIds = userIds;
    }

}
