package com.jacevys.intelligenceapi.controller;

import com.jacevys.intelligenceapi.service.IntelligenceService;
import com.jacevys.intelligenceapi.common.response.DefaultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.scheduling.annotation.Async;

@RestController
@RequestMapping("/v1")
public class IntelligenceController{
    private static final Logger logger = LoggerFactory.getLogger(IntelligenceController.class);
    private static final int API_TIMEOUT = 10; // 設定秒數超時時間
    DefaultResponse defaultResponse = new DefaultResponse();

    @Autowired
    public IntelligenceService intelligenceservice;

    @GetMapping(value = "/get_label", produces = "application/json")
    @Async
    public CompletableFuture<Map<String, Object>> getLabel(
        @RequestParam String chainName,
        @RequestParam String address,
        @RequestParam String sourceListCode,
        @RequestParam boolean searchFlag,
        @RequestParam boolean quickMode
    ) {
        return intelligenceservice.processRequest(
            chainName,
            address,
            sourceListCode,
            searchFlag,
            quickMode
        ).orTimeout(API_TIMEOUT, TimeUnit.SECONDS) // 設置超時
        .thenApply(result -> {
            logger.info("Response: {}", result);
            return result;
        }).exceptionally(ex -> {
            // 捕獲超時或異常，返回預設結果
            if (ex instanceof TimeoutException) {
                logger.info("Request timed out for: {}, {}, {}, {}, {}", 
                            chainName, address, sourceListCode, searchFlag, quickMode);
            } else {
                logger.error("Unexpected error: {}", ex.getMessage());
            }
            return defaultResponse.getDefaultResponse();
        });
    }
}
// ok