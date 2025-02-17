package com.jacevys.intelligenceapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jacevys.intelligenceapi.common.response.DefaultResponse;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import org.bson.Document;
import com.jacevys.intelligenceapi.repository.MisttrackApi;

@Service
public class IntelligenceService {
    public static final String[] CHAIN_LIST = {"bitcoin", "ethereum", "tron"};
    public static final DefaultResponse defaultResponse = new DefaultResponse();
    private static final String MONGO_URI = "mongodb://mongo-intelligence:27017/";

    // @Autowired
    // private GetLabelCib getLabelCib;
    // private MongoClient mongoclient = MongoClients.create(MONGO_URI);
    // private MongoDatabase database = mongoclient.getDatabase("blockchain_security");

    @Async
    public CompletableFuture<Map<String, Object>> processRequest(
        String chainName,
        String address,
        String sourceListCode,
        boolean searchFlag,
        boolean quickMode
    ) {
        if (!isValidChain(chainName)) {
            return CompletableFuture.completedFuture(defaultResponse.getDefaultResponse());
        }
        // 模擬業務處理，實際業務邏輯可以在這裡進行
        return CompletableFuture.supplyAsync(() -> {
            // MongoCollection<Document> collection = database.getCollection(chainName);
            // Document document = collection.find(eq("address", address)).first();

            Map<String, String> apiTokens = Map.of("misttrack", "");
            MisttrackApi misttrackApi = new MisttrackApi(apiTokens);
            Map<String, Object> labelDict = new HashMap<>();
            misttrackApi.getLabel(chainName, address, 10, labelDict);
            System.out.println("damn" + labelDict.toString());
            return labelDict;

            // // 設置返回的 response 內容
            // return defaultResponse;
            // Map<String, Object> something = new HashMap<>();
            // something.put("blockchain_security", List.of(
            //     new HashMap<String, Object>() {{
            //         put("type", "Unknown");
            //         put("name", "Unknown");
            //         put("labels", List.of());
            //     }}
            // ));
            // return something;
        });
    }

    private boolean isValidChain(String chainName) {
        for (String chain : CHAIN_LIST) {
            if(chainName.equals(chain)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> processLabel(Map<String, Object> labelDict) {
        Map<String, Object> returnedDict = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        data.put("blockchain_security", new ArrayList<>());
        data.put("chainanalysis", new ArrayList<>());
        data.put("qlue", new ArrayList<>());
        data.put("smart_contract", false);
        data.put("black_list", false);

        returnedDict.put("status", true);
        returnedDict.put("message", "Get address category from several APIs.");
        returnedDict.put("data", data);

        for(String key : data.keySet()) {
            if ("blockchain_security".equals(key)) {
                if (labelDict.containsKey("misttrack")) {
                    data.put(key, labelDict.get("misttrack"));
                } else {
                    data.put(key, new ArrayList<>());
                }
            } else if (labelDict.containsKey(key)) {
                    data.put(key, labelDict.get(key));
            }
        }

        return returnedDict;
    }
}