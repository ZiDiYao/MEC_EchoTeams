
package com.echoteam.MEC.llm.client;
import com.echoteam.MEC.common.LLMCode;
import com.echoteam.MEC.llm.configs.LlmModelProperties;
import com.echoteam.MEC.llm.configs.LlmModelProperties.ModelConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component("deepSeekClient")
public class DeepSeekClientImpl extends BaseLLMClient {

    private final LlmModelProperties props;
    private final ObjectMapper mapper = new ObjectMapper();

    public DeepSeekClientImpl(LlmModelProperties props) {
        this.props = props;
    }

    private ModelConfig cfg() {
        ModelConfig c = props.getModels() != null ? props.getModels().get(LLMCode.DEEPSEEK.name()) : null;
        if (c == null) c = props.getActiveModelConfig(); // 兜底按 active
        if (c == null) throw new IllegalStateException("No DeepSeek config under mec.llm.models.DEEPSEEK");
        return c;
    }

    @Override
    protected String getApiUrl() {
        return cfg().getApiUrl();
    }

    @Override
    protected String getApiKey() {
        return cfg().getApiKey();
    }

    @Override
    protected Map<String, Object> buildRequestBody(String prompt, String model) {

        Map<String, Object> sys = Map.of(
                "role", "system",
                "content", "You are a precise JSON-only analyzer. Respond with a single JSON object and nothing else."
        );
        Map<String, Object> usr = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("temperature", cfg().getTemperature());
        body.put("messages", new Object[]{ sys, usr });
        return body;
    }

    @Override
    protected String extractTextFromResponse(String responseJson) throws IOException {

        JsonNode root = mapper.readTree(responseJson);

        if (root.has("incidentType") || root.has("requestId") || root.has("address")) {
            return responseJson;
        }

        JsonNode content = root.path("choices").path(0).path("message").path("content");
        if (!content.isMissingNode() && !content.asText().isBlank()) {
            return content.asText();
        }

        JsonNode text = root.path("choices").path(0).path("text");
        if (!text.isMissingNode() && !text.asText().isBlank()) {
            return text.asText();
        }
        throw new IOException("DeepSeek response missing assistant content: " + responseJson);
    }
}
