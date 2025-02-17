package com.jacevys.intelligenceapi.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalTime;
import java.util.*;
import java.time.LocalTime;

class MisttrackApiTest {

    private MisttrackApi misttrackApi;
    private OkHttpClient mockClient;

    @BeforeEach
    void setUp() {
        Map<String, String> apiTokens = Map.of("misttrack", "dummy_api_key");
        misttrackApi = new MisttrackApi(apiTokens);

        // Mock OkHttpClient
        mockClient = mock(OkHttpClient.class);
        misttrackApi.setClient(mockClient); // 設置 mocked client
    }

    // @Test
    void testGetLabel_Success() throws IOException {
        // Mock successful API response
        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("https://openapi.misttrack.io/v1/address_labels").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("{\"success\":true,\"message\":\"\",\"data\":{\"label_type\":\"Exchange\",\"label_list\":[\"tag1\",\"tag2\"]}}", MediaType.get("application/json")))
                .build();

        Call mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        Map<String, Object> labelDict = new HashMap<>();
        misttrackApi.getLabel("bitcoin", "34xp4vRoCGJym3xR7yCVPFHoCNxv4Twseo", 10, labelDict);

        assertTrue(labelDict.containsKey("misttrack"));
        // assertEquals("EXCHANGE", ((Map<String, Object>) labelDict.get("misttrack")).get("type"));
    }

    // @Test
    void testGetLabel_HttpError() throws IOException {
        // Mock failed API response
        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("https://openapi.misttrack.io/v1/address_labels").build())
                .protocol(Protocol.HTTP_1_1)
                .code(500)
                .message("Internal Server Error")
                .body(ResponseBody.create("", MediaType.get("application/json")))
                .build();

        Call mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        Map<String, Object> labelDict = new HashMap<>();
        misttrackApi.getLabel("bitcoin", "dummy_address", 10, labelDict);

        assertTrue(labelDict.containsKey("misttrack"));
        assertEquals("Unknown", ((Map<String, Object>) ((List<Map<String, Object>>) labelDict.get("misttrack")).get(0)).get("type"));
    }

    @Test
    void virtualThread() {
        // 使用虛擬線程執行任務
        for (int i = 0; i < 100000; i++) {
            Thread.startVirtualThread(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " started at " + LocalTime.now());

                try {
                    // 模擬任務
                    Thread.sleep(2000); // 虛擬線程不會佔用實體線程資源
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(threadName + " finished at " + LocalTime.now());
            });
        }

        System.out.println("Main thread finished at " + LocalTime.now());
    }
}
// ok