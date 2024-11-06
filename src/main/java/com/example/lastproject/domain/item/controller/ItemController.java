package com.example.lastproject.domain.item.controller;

import com.example.lastproject.common.enums.CustomMessage;
import com.example.lastproject.domain.item.dto.response.ItemResponse;
import com.example.lastproject.domain.item.service.ItemService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/items")
@RestController
public class ItemController {

    private final ItemService itemService;

    /**
     * 표준 품목 업데이트
     *
     * @return 성공메시지
     */
    @PostMapping("/open-api")
    public ResponseEntity<CustomMessage> getItemFromOpenApi() {
        itemService.getItemFromOpenApi();
        return ResponseEntity.ok(CustomMessage.ON_SUCCESS);
    }

    /**
     * @param keyword 카테고리 검색어
     * @return 조회된 아이템 목록
     */
    @GetMapping
    public ResponseEntity<List<ItemResponse>> searchItems(
            @RequestParam
            @NotBlank(message = "공백은 허용되지 않습니다.") String keyword
    ) {
        List<ItemResponse> response = itemService.searchItems(keyword);
        return ResponseEntity.ok(response);
    }

}
