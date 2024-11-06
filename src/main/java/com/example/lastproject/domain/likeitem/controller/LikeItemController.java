package com.example.lastproject.domain.likeitem.controller;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.enums.CustomMessage;
import com.example.lastproject.domain.likeitem.dto.response.LikeItemResponse;
import com.example.lastproject.domain.likeitem.service.LikeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like-items")
public class LikeItemController {

    private final LikeItemService likeItemService;

    /**
     * 즐겨찾기 품목 등록
     *
     * @param authUser 즐겨찾기 품목 등록할 유저 대상
     * @param itemId   즐겨찾기 등록할 품목 아이디
     * @return 성공메시지
     */
    @PostMapping("/{itemId}")
    public ResponseEntity<CustomMessage> bookmarkItem(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long itemId
    ) {
        likeItemService.bookmarkItem(authUser, itemId);
        return ResponseEntity.ok(CustomMessage.ON_SUCCESS);
    }

    /**
     * 즐겨찾기 품목 리스트 조회
     *
     * @param authUser 품목 조회할 유저 대상
     * @return 조회된 즐겨찾기 품록 객체리스트
     */
    @GetMapping
    public ResponseEntity<List<LikeItemResponse>> getLikeItems(@AuthenticationPrincipal AuthUser authUser) {
        List<LikeItemResponse> response = likeItemService.getLikeItems(authUser);
        return ResponseEntity.ok(response);
    }

    /**
     * 즐겨찾기 품목 삭제
     *
     * @param authUser   즐겨찾기 삭제할 유저
     * @param likeItemId 삭제할 즐겨찾기 아이디
     * @return 성공메시지
     */
    @DeleteMapping("/{likeItemId}")
    public ResponseEntity<CustomMessage> deleteLikeItem(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long likeItemId
    ) {
        likeItemService.deleteLikeItem(authUser, likeItemId);
        return ResponseEntity.ok(CustomMessage.ON_SUCCESS);
    }

}
