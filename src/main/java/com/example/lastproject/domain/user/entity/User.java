package com.example.lastproject.domain.user.entity;

import com.example.lastproject.common.Timestamped;
import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.likeitem.entity.LikeItem;
import com.example.lastproject.domain.user.dto.request.UserUpdateRequest;
import com.example.lastproject.domain.user.enums.UserRole;
import com.example.lastproject.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private BigDecimal latitude;

    @Column(nullable = false)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVATED;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<LikeItem> likeItems = new ArrayList<>();

    public User(String email, String password, String nickname, String address, BigDecimal latitude, BigDecimal longitude, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userRole = userRole;
    }

    private User(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }

    public static User fromAuthUser(AuthUser authUser) {

        /*
        authUser 는 로그인이 완료되었을 때만 생기는 DTO, 애초에 null 일 수 없는 이유
        -> 로그인 실패 처리가 될 것임.
        */

        return new User(
                authUser.getUserId(),
                authUser.getEmail(),
                UserRole.of(authUser.getAuthorities().stream()
                        .findFirst()
                        .get()
                        .getAuthority())
        );
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void update(UserUpdateRequest request) {
        this.nickname = request.getNickname();
        this.address = request.getAddress();
    }

    public void toggleDelete() {
        this.userStatus = UserStatus.DELETED;
    }

}
