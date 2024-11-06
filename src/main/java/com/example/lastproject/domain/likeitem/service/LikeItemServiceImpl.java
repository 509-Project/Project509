package com.example.lastproject.domain.likeitem.service;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.item.entity.Item;
import com.example.lastproject.domain.item.service.ItemService;
import com.example.lastproject.domain.likeitem.dto.response.LikeItemResponse;
import com.example.lastproject.domain.likeitem.entity.LikeItem;
import com.example.lastproject.domain.likeitem.repository.LikeItemRepository;
import com.example.lastproject.domain.user.entity.User;
import com.example.lastproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeItemServiceImpl implements LikeItemService {
    private final LikeItemRepository likeItemRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    /**
     * @param authUser 즐겨찾기 품목 등록할 유저
     * @param itemId   즐겨찾기 등록할 품목 아이디
     */
    @Transactional
    public void bookmarkItem(AuthUser authUser, Long itemId) {
        User user = User.fromAuthUser(authUser);

        // 이미 등록된 즐겨찾기 품목인지 검증
        boolean exists = likeItemRepository.existsByUserBookmarkedItem(user.getId(), itemId);
        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_ITEM);
        }

        // 등록할 품목엔티티
        Item item = itemService.validateItem(itemId);

        LikeItem likeItem = new LikeItem(user, item);
        likeItemRepository.save(likeItem);
    }

    /**
     * @param authUser 즐겨찾기 품목 조회할 유저
     * @return 조회된 품목 리스트
     */
    public List<LikeItemResponse> getLikeItems(AuthUser authUser) {
        long userId = authUser.getUserId();
        List<LikeItemResponse> results = likeItemRepository.getBookmarkedItems(userId);

        return results;
    }

    /**
     * @param authUser   즐겨찾기 삭제할 유저
     * @param likeItemId 삭제할 즐겨찾기 아이디
     */
    @Transactional
    public void deleteLikeItem(AuthUser authUser, Long likeItemId) {
        // 나의 즐겨찾기 품목인지 검증
        long userId = authUser.getUserId();
        boolean exists = likeItemRepository.existsByUserBookmarkedItem(userId, likeItemId);
        if (!exists) {
            throw new CustomException(ErrorCode.ITEM_NOT_FOUND);
        }

        likeItemRepository.deleteById(likeItemId);
    }

}
