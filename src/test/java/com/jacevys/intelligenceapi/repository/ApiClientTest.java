package com.jacevys.intelligenceapi.repository;

import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApiClientTest {
    private ApiClient apiClient;

    @Mock
    private OkHttpClient mockClient;

    @Mock
    private Call mockCall;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Map<String, String> apiTokens = new HashMap<>();
        apiTokens.put("testToken", "value");
        
        apiClient = new ApiClient(apiTokens);

        // Mock OkHttpClient
        mockClient = mock(OkHttpClient.class);
        apiClient.setClient(mockClient); // 設置 mocked client
    }

    @Test
    public void testGet_Success() throws IOException, InterruptedException {
        // 模擬成功的 API 響應
        // Response mockResponse = new Response.Builder()
        //         .request(new Request.Builder().url("https://openapi.misttrack.io/v1/address_labels").build())
        //         .protocol(Protocol.HTTP_1_1)
        //         .code(200)
        //         .message("OK")
        //         .body(ResponseBody.create("{\"result\": \"success\"}", MediaType.parse("application/json")))
        //         .build();

        // when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        // when(mockCall.execute()).thenReturn(mockResponse);

        Map<String, String> params = Map.of(
            "coin", "BTC",
            "address", "34xp4vRoCGJym3xR7yCVPFHoCNxv4Twseo",
            "api_key", ""
        );

        ApiClient.ApiResponse response = apiClient.get("https://openapi.misttrack.io/v1/address_labels", null, params);

        System.out.println(response.toMap());
        assertTrue(response.isSuccess());
    }
}
// ok