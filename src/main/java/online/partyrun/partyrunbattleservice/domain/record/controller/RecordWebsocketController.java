package online.partyrun.partyrunbattleservice.domain.record.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import online.partyrun.partyrunbattleservice.domain.record.dto.RecordRequests;
import online.partyrun.partyrunbattleservice.domain.record.dto.RunnerDistanceResponse;
import online.partyrun.partyrunbattleservice.domain.record.service.RecordService;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordWebsocketController {

    RecordService recordService;
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/battle/{battleId}/record")
    public void calculateDistance(
            @DestinationVariable String battleId,
            Authentication auth,
            @Valid RecordRequests request) {
        log.info(request.getGpsDatas().toString());
        final String runnerId = auth.getName();
        RunnerDistanceResponse response =
                recordService.calculateDistance(battleId, runnerId, request);
        messagingTemplate.convertAndSend("/topic/battle/" + battleId, response);
    }
}
