package com.example.real_time_order_processing.ai.controller;

import jakarta.validation.Valid;
import com.example.real_time_order_processing.ai.dto.ChatRequest;
import com.example.real_time_order_processing.auth.repository.UserInfoRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/ai")
public class ChatController
{
    private final ChatClient chatClient;
    private final UserInfoRepository userInfoRepository;

    ChatController(
            @Qualifier("helpdeskChatClient") ChatClient chatClient,
            UserInfoRepository userInfoRepository)
    {
        this.chatClient = chatClient;
        this.userInfoRepository = userInfoRepository;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@Valid @RequestBody ChatRequest request)
    {
        String email = getAuthenticatedEmail();
        String userContext = buildUserContext(email);
        String response = chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, email))
                .user(userContext + request.getMessage())
                .call()
                .content();
        return ResponseEntity.ok(response);
    }

    private String getAuthenticatedEmail()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
        {
            throw new ResponseStatusException(UNAUTHORIZED, "Authentication required.");
        }
        return authentication.getName();
    }

    private String buildUserContext(String email)
    {
        return userInfoRepository.findByEmail(email)
                .map(user -> "Logged-in customer email: " + email
                        + ". User ID for createTicket/getTicketStatus/updateTicketPriority: " + user.getId()
                        + ". Do not ask for phone or email — use this user ID and email when escalating."
                        + " Build the issue description from the conversation and call createTicket immediately.\n\n")
                .orElse("Logged-in customer email: " + email
                        + ". User ID could not be resolved — do not call ticket tools.\n\n");
    }
}
