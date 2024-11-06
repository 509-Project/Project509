package com.example.lastproject.domain.item.service;

import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.item.dto.response.ItemResponse;
import com.example.lastproject.domain.item.entity.Item;
import com.example.lastproject.domain.item.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    JobLauncher jobLauncher;

    @Mock
    JobRegistry jobRegistry;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    @DisplayName("품목 검색 반환 테스트")
    void 품목_검색시_DTO_리스트를_정상적으로_반환한다() {

        // given,
        ItemResponse firstResponse = new ItemResponse(1, "사과", "홍사과");
        ItemResponse secondResponse = new ItemResponse(2, "사과", "청사과");
        List<ItemResponse> dtoList = List.of(firstResponse, secondResponse);
        given(itemRepository.searchItemsByKeywordInCategory(anyString())).willReturn(dtoList);

        // when
        List<?> results = itemService.searchItems(anyString());

        // than
        assertInstanceOf(ItemResponse.class, results.get(0));
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("품목 검색 예외 반환 테스트")
    void 품목_검색시_결과값이_없으면_얘외를_반환한다() {

        // given
        List<ItemResponse> dtoList = new ArrayList<>();
        given(itemRepository.searchItemsByKeywordInCategory(anyString())).willReturn(dtoList);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> itemService.searchItems(anyString()));

        // then
        assertEquals("조회되는 품목이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("정상적으로 엔티티를 반환하는 경우")
    void 품목_조회시_정상적으로_엔티티를_반환한다() {

        //given
        Item item = new Item(1L, "사과", "청사과");
        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item));

        //when
        Item result = itemService.validateItem(anyLong());

        //then
        assertEquals(item, result);
    }

    @Test
    @DisplayName("품목 조회 테스트")
    void 품목_조회시_일치하는_엔티티가_없으면_예외를_반환한다() {

        //given
        given(itemRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> itemService.validateItem(anyLong()));

        //then
        assertEquals("조회되는 품목이 없습니다.", exception.getMessage());
    }

}
