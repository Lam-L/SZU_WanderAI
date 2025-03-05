package com.wanderai.controller;

import com.wanderai.dto.AiChatRequest;
import com.wanderai.dto.AiChatResponse;
import com.wanderai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> getChatResponse(@RequestBody AiChatRequest request) {
        return ResponseEntity.ok(aiService.getChatResponse(request));
    }

    @GetMapping("/location")
    public ResponseEntity<AiChatResponse> getLocationSuggestions(
            @RequestParam double lat,
            @RequestParam double lng) {
        return ResponseEntity.ok(aiService.getLocationSuggestions(lat, lng));
    }

    @GetMapping("/route")
    public ResponseEntity<AiChatResponse> getRoutePlanning(@RequestParam String destination) {
        return ResponseEntity.ok(aiService.getRoutePlanning(destination));
    }
}