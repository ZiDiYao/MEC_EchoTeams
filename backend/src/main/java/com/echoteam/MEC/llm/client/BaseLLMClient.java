
package com.echoteam.MEC.llm.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public abstract class BaseLLMClient implements LLMClient {

    protected static final ObjectMapper MAPPER = new ObjectMapper();
    protected static final MediaType JSON = MediaType.parse("application/json");

    /** Shared OkHttp client with reasonable defaults. */
    protected final OkHttpClient http;

    protected BaseLLMClient() {
        this.http = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .callTimeout(90, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }


    @Override
    public final String callLLM(String prompt, String model) {
        try {
            String apiUrl = getApiUrl();
            String apiKey = getApiKey();

            if (apiUrl == null || apiUrl.isBlank()) {
                throw new IllegalStateException("LLM API URL is missing.");
            }
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalStateException("LLM API Key is missing.");
            }

            // Build JSON request body
            Map<String, Object> body = buildRequestBody(prompt, model);
            String bodyJson = MAPPER.writeValueAsString(body);

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(bodyJson, JSON))
                    .build();

            try (Response response = http.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String resp = response.body() != null ? response.body().string() : "";
                    throw new IOException("LLM API call failed: " +
                            response.code() + " " + response.message() +
                            " body=" + resp);
                }

                String resp = response.body() != null ? response.body().string() : "";
                // Let subclass decide how to extract the assistant text
                return extractTextFromResponse(resp);
            }
        } catch (Exception e) {
            throw new RuntimeException("LLM API calling error: " + e.getMessage(), e);
        }
    }


    protected abstract String getApiUrl();

    protected abstract String getApiKey();

    protected abstract Map<String, Object> buildRequestBody(String prompt, String model);
    protected abstract String extractTextFromResponse(String responseJson) throws IOException;
}
