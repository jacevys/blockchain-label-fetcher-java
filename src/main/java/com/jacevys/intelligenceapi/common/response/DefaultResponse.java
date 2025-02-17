package com.jacevys.intelligenceapi.common.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DefaultResponse {
    private Map<String, Object> defaultResponse;
    private Map<String, Object> responseBody;

    public DefaultResponse () {
        Map<String, Object> defaultResponse = new HashMap<>();
        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("blockchain_security", List.of(
            new HashMap<String, Object>() {{
                put("type", "Unknown");
                put("name", "Unknown");
                put("labels", List.of());
            }}
        ));
        responseBody.put("chainanalysis", List.of(
            new HashMap<String, Object>() {{
                put("type", "Unknown");
                put("name", "Unknown");
            }}
        ));
        responseBody.put("qlue", List.of(
            new HashMap<String, Object>() {{
                put("type", "Unknown");
                put("name", "Unknown");
                put("labels", List.of());
            }}
        ));
        responseBody.put("smart_contract", false);
        responseBody.put("black_list", false);
        responseBody.put("timeout", true);
        defaultResponse.put("status", true);
        defaultResponse.put("message", "Request processing time out");
        defaultResponse.put("data", responseBody);
    }

    public Map<String, Object> getDefaultResponse() {
        return defaultResponse;
    }

    @Override
    public String toString() {
        return "DefaultResponse{" +
               "defaultResponse=" + defaultResponse +
               '}';
    }
}
// ok