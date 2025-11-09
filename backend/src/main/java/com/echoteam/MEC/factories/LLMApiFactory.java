package com.echoteam.MEC.factories;
import com.echoteam.MEC.common.LLMCode;
import com.echoteam.MEC.llm.strategy.LLMApi;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class LLMApiFactory {

    private final Map<LLMCode, LLMApi> strategyMap = new HashMap<>();
    public LLMApiFactory(List<LLMApi> strategies) {
        for (LLMApi api : strategies) {
            System.out.println("[LLMApiFactory] Registered strategy: " + api.APIName());
            strategyMap.put(api.APIName(), api);
        }
    }

    public LLMApi getLLMApi(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("LLM model name cannot be null or empty");
        }
        LLMCode code;
        try {
            code = LLMCode.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown LLM model name: " + name);
        }
        LLMApi api = strategyMap.get(code);
        if (api == null) {
            throw new IllegalStateException("No LLM strategy registered for: " + name);
        }

        return api;
    }
}
