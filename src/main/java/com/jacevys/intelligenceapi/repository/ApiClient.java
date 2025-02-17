package com.jacevys.intelligenceapi.repository;

import okhttp3.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiClient{
    private final Map<String, String> apiTokens;
    protected final OkHttpClient client;
    private final int maxRetries = 5;
    private final int retryDelay = 1;
    private final Logger logger = Logger.getLogger(ApiClient.class.getName());

    public ApiClient(Map<String, String> apiTokens) {
        this.apiTokens = apiTokens;
        this.client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build();
    }

    public ApiResponse get(String url, Map<String, String> headers, Map<String, String> params) throws IOException, InterruptedException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                urlBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build());

        if (headers != null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return executeWithRetry(requestBuilder.build());
    }

    public ApiResponse post(
        String url, 
        Map<String, String> headers, 
        Map<String, String> params, 
        String body // 假設請求主體是 JSON 字符串
    ) throws IOException, InterruptedException {
        // 构建 URL
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                urlBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        // 构建请求体
        RequestBody requestBody = RequestBody.create(body, MediaType.parse("application/json"));

        // 构建 Request
        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .post(requestBody);

        // 添加请求头
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // 调用 executeWithRetry 方法执行请求
        return executeWithRetry(requestBuilder.build());
    }

    // 在 executeWithRetry 中处理响应数据
    private ApiResponse executeWithRetry(Request request) throws IOException, InterruptedException {
        int attempt = 0;
        ApiResponse responseJson = new ApiResponse();

        ObjectMapper objectMapper = new ObjectMapper();  // 创建 Jackson 的 ObjectMapper 实例

        while (attempt < maxRetries) {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    responseJson.setSuccess(true);
                    // 解析 JSON 响应
                    responseJson.setData(objectMapper.readValue(response.body().string(), Map.class));
                    return responseJson;
                } else if (response.code() == 429) {
                    attempt++;
                    logger.info(String.format("Request failed with status %d", response.code()));
                    TimeUnit.SECONDS.sleep(retryDelay);
                } else {
                    responseJson.setSuccess(false);
                    responseJson.setData(null);
                    logger.info(String.format("Request failed with status %d", response.code()));
                    return responseJson;
                }
            } catch (IOException | InterruptedException e) {
                if (++attempt >= maxRetries) {
                    responseJson.setSuccess(false);
                    responseJson.setError(e.getMessage());
                    return responseJson;
                }
                TimeUnit.SECONDS.sleep(retryDelay);
            }
        }
        return responseJson;
    }

    public void setClient(OkHttpClient client) {
        client = this.client; // 设置继承的客户端对象
    }

    public static class ApiResponse{
        private boolean success;
        private Map<String, Object> data;
        private String error;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
        public Map<String, Object> toMap() {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", this.success);
            responseMap.put("data", this.data);
            responseMap.put("error", this.error);
            return responseMap;
        }
    }
}
// ok