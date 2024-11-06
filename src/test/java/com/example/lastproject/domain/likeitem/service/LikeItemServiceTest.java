package com.example.lastproject.domain.likeitem.service;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.item.entity.Item;
import com.example.lastproject.domain.item.service.ItemServiceImpl;
import com.example.lastproject.domain.likeitem.dto.response.LikeItemResponse;
import com.example.lastproject.domain.likeitem.entity.LikeItem;
import com.example.lastproject.domain.likeitem.repository.LikeItemRepository;
import com.example.lastproject.domain.user.entity.User;
import com.example.lastproject.domain.user.enums.UserRole;
import com.example.lastproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LikeItemServiceTest {

    @Mock
    LikeItemRepository likeItemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemServiceImpl itemService;

    @InjectMocks
    LikeItemServiceImpl likeItemService;

    @Test
    @DisplayName("즐겨찾기 등록 테스트")
    void 정삭적으로_메서드가_호출된다() {

        // given
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_USER);
        User user = User.fromAuthUser(authUser);
        Long itemId = 1L;
        Item item = new Item("사과", "홍옥");
        given(likeItemRepository.existsByUserBookmarkedItem(1L, 1L)).willReturn(false);
        given(itemService.validateItem(itemId)).willReturn(item);

        // when
        likeItemService.bookmarkItem(authUser, 1L);

        // then
        verify(likeItemRepository, times(1)).save(any(LikeItem.class));
    }

    @Test
    @DisplayName("이미 등록된 물품의 경우")
    void 이미_등록된_물품은_예외를_반환한다() {

        // given
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_USER);
        User user = User.fromAuthUser(authUser);
        given(likeItemRepository.existsByUserBookmarkedItem(user.getId(), 1L)).willReturn(true);

        //when
        CustomException exception = assertThrows(CustomException.class, () -> likeItemService.bookmarkItem(authUser, 1L));

        //then
        assertEquals("이미 등록된 물품입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("즐겨찾기 품목 조회 테스트")
    void 즐겨찾기_품목_조회시_DTO를_반환한다() {

        // given
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_USER);
        LikeItemResponse firstResponse = new LikeItemResponse(1, "사과", "홍사과");
        LikeItemResponse secondResponse = new LikeItemResponse(2, "사과", "청사과");
        List<LikeItemResponse> dtoList = List.of(firstResponse, secondResponse);
        given(likeItemRepository.getBookmarkedItems(anyLong())).willReturn(dtoList);

        // when
        List<?> results = likeItemService.getLikeItems(authUser);

        // then
        assertInstanceOf(LikeItemResponse.class, results.get(0));
        verify(likeItemRepository, times(1)).getBookmarkedItems(anyLong());
    }

    @Test
    @DisplayName("즐겨찾기 품목 삭제 테스트")
    void 즐겨찾기_품목이_정상적으로_삭제된다() {

        // given
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_USER);
        long userId = authUser.getUserId();
        long likeItemId = 1L;
        given(likeItemRepository.existsByUserBookmarkedItem(userId, likeItemId)).willReturn(true);

        // when
        likeItemService.deleteLikeItem(authUser, likeItemId);

        // then
        verify(likeItemRepository, times(1)).deleteById(likeItemId);
    }

    @Test
    @DisplayName("즐겨찾기 품목 조회 테스트")
    void 삭제할_품목이_없으면_예외를_반환한다() {

        // given
        AuthUser authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_USER);
        long userId = authUser.getUserId();
        long likeItemId = 1L;
        given(likeItemRepository.existsByUserBookmarkedItem(userId, likeItemId)).willReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> likeItemService.deleteLikeItem(authUser, likeItemId));

        // then
        assertEquals("조회되는 품목이 없습니다.", exception.getMessage());
    }
}
