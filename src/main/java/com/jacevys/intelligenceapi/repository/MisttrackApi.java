package com.jacevys.intelligenceapi.repository;

import okhttp3.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class MisttrackApi extends ApiClient {
    private String apiKey;
    private List<Map<String, Object>> defaultLabel;
    private Map<String, String> mapping;

    public MisttrackApi(Map<String, String> apiTokens) {
        super(apiTokens);
        this.apiKey = apiTokens.get("misttrack");
        this.defaultLabel = List.of(
            Map.of(
                "type", "Unknown",
                "name", "Unknown",
                "labels", List.of()
            )
        );
        this.mapping = Map.of(
            "bitcoin", "BTC",
            "ethereum", "ETH",
            "tron", "TRX"
        );
    }

    public void getLabel(String chainName, String address, int timeout, Map<String, Object> labelDict) {
        String url = "https://openapi.misttrack.io/v1/address_labels";
        String coin = mapping.getOrDefault(chainName, "");
        Map<String, String> headers = Map.of(
        );
        Map<String, String> payload = Map.of(
            "coin", coin,
            "address", address,
            "api_key", apiKey
        );

        ApiResponse result;
        try {
            result = this.get(url, headers, payload); // 使用ApiClient的get方法发起请求
        } catch (IOException | InterruptedException e) {
            handleError(labelDict, "Error while calling API: " + e.getMessage());
            return;
        }

        if (!result.isSuccess()) {
            handleError(labelDict, "HTTP error: " + result.getError());
            return;
        }

        Map<String, Object> response = result.getData();
        Map<String, Object> data = (Map) response.get("data");
        List<String> labelList = (List<String>) data.get("label_list");
        String labelType = (String) data.get("label_type");
        System.out.println(labelList);

        if ((boolean) response.get("success")) {
            Optional.ofNullable(labelType).filter(type -> !type.isEmpty()).orElse("Unknown");

            Map<String, Object> misttrackData = new HashMap<>();
            misttrackData.put("type", labelType);
            misttrackData.put("labels", labelList);
            labelDict.put("misttrack", misttrackData);
            labelDict.put("timestamp_misttrack_last_edit", System.currentTimeMillis() / 1000);
        } else {
            handleError(labelDict, "API returned error or no data.");
        }
    }

    public void handleError(Map<String, Object> labelDict, String errorMsg) {
        System.err.println("Misttrack API Error: " + errorMsg);
        labelDict.put("misttrack", defaultLabel);
        labelDict.put("timestamp_misttrack_last_edit", System.currentTimeMillis() / 1000);
    }

    public Map<String, Object> parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Map<String, Object> result = new HashMap<>();
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                // 如果值是嵌套的 JSON 對象或 JSON 字符串，進一步解析
                if (value instanceof JSONObject) {
                    value = parseJson(value.toString());
                } else if (value instanceof String && isJsonString((String) value)) {
                    value = parseJson((String) value);
                }
                result.put(key, value);
            }
            System.out.println("parseJson返回的JSONObject: " + result.toString());
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    // 檢查字符串是否是 JSON
    private boolean isJsonString(String str) {
        try {
            new JSONObject(str);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    public void setClient(OkHttpClient client) {
        client = this.client; // 设置继承的客户端对象
    }
}