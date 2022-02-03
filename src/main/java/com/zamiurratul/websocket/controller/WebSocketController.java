package com.zamiurratul.websocket.controller;

import com.zamiurratul.websocket.MyThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Executor myExecutors = Executors.newFixedThreadPool(30, new MyThreadFactory());

    @MessageMapping("/info")
    @SendTo("/topic/info-replies")
    public String broadcastNews(@Payload String message) {
        System.out.println("Received: " + message);
        return "Data from backend server... " + new Date();
    }

    @MessageMapping("/personal")
    public void userSpecificMessage(@Payload String message, @Header("simpSessionId") String sessionId) {
        System.out.println("[[ USER ]]: " + sessionId);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        runService(() -> task1(sessionId, headerAccessor));
//        simpMessagingTemplate.convertAndSendToUser(sessionId, "/user/queue/specific-user", rand);
//        simpMessagingTemplate.convertAndSendToUser(sessionId, "/user/queue/specific-user", rand, headerAccessor.getMessageHeaders());
    }

    public void task1(String sessionId, SimpMessageHeaderAccessor headerAccessor) {
        for (int i = 0; i < 1000; i++) {
            String rand = randomString();
            rand = "User " + sessionId + " Val " + rand;
            simpMessagingTemplate.convertAndSendToUser(sessionId, "/user/queue/specific-user", rand, headerAccessor.getMessageHeaders());
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {

            }
        }
    }

    //    @Scheduled(cron = "15 * * * * *")
    @Scheduled(fixedRate = 750, initialDelay = 1000)
    public void scheduled1() {
        simpMessagingTemplate.convertAndSend("/topic/info-replies-1", randomString());
    }

    @Scheduled(fixedRate = 1000, initialDelay = 1000)
    public void scheduled2() {
        simpMessagingTemplate.convertAndSend("/topic/info-replies-2", randomString());
    }

    private String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

    private void runService(Runnable runnable) {
        CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return true;
        }, myExecutors).exceptionally(ex -> {
            System.out.println("Exception: " + ex.getMessage());
            return false;
        });
    }
}
