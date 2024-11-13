package com.example.lastproject.domain.chat.controller;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.config.JwtAuthenticationToken;
import com.example.lastproject.config.JwtUtil;
import com.example.lastproject.config.SecurityConfig;
import com.example.lastproject.domain.chat.dto.ChatRoomResponse;
import com.example.lastproject.domain.chat.enums.ChatRoomStatus;
import com.example.lastproject.domain.chat.service.ChatRoomService;
import com.example.lastproject.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ChatRoomController.class})
@Import({SecurityConfig.class, JwtUtil.class})
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatRoomService chatRoomService;

    private JwtAuthenticationToken userAuthenticationToken;

    @Test
    public void getChatRooms_성공() throws Exception {

        AuthUser normalUser = new AuthUser(2L, "user@example.com", UserRole.ROLE_USER);
        userAuthenticationToken = new JwtAuthenticationToken(normalUser);

        List<ChatRoomResponse> chatRoomResponses = List.of(
                new ChatRoomResponse(1L, "name1", 1L, ChatRoomStatus.ACTIVATED),
                new ChatRoomResponse(2L, "name2", 2L, ChatRoomStatus.ACTIVATED)
        );
        given(chatRoomService.getChatRooms(normalUser)).willReturn(chatRoomResponses);

        mockMvc.perform(get("/chatrooms")
                        .with(authentication(userAuthenticationToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("name1")))
                .andExpect(jsonPath("$[0].status", is(ChatRoomStatus.ACTIVATED.name())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[1].status", is(ChatRoomStatus.ACTIVATED.name())));
    }

    @Test
    public void createChatRoom_성공() throws Exception {

        AuthUser normalUser = new AuthUser(2L, "user@example.com", UserRole.ROLE_USER);
        userAuthenticationToken = new JwtAuthenticationToken(normalUser);

        Long partyId = 1L;
        ChatRoomResponse response = new ChatRoomResponse(1L, "name1", partyId, ChatRoomStatus.ACTIVATED);
        given(chatRoomService.createChatRoom(partyId, normalUser)).willReturn(response);

        mockMvc.perform(post("/chatrooms/{partyId}", partyId)
                        .with(authentication(userAuthenticationToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name1")))
                .andExpect(jsonPath("$.status", is(ChatRoomStatus.ACTIVATED.name())));
    }

    @Test
    public void deleteChatRoom_성공() throws Exception {

        AuthUser normalUser = new AuthUser(2L, "user@example.com", UserRole.ROLE_USER);
        userAuthenticationToken = new JwtAuthenticationToken(normalUser);

        Long chatRoomId = 1L;
        doNothing().when(chatRoomService).deleteChatRoom(chatRoomId, normalUser);

        mockMvc.perform(delete("/chatrooms/{chatRoomId}", chatRoomId)
                        .with(authentication(userAuthenticationToken)))
                .andExpect(status().isNoContent());
    }

}