
package com.echoteam.MEC.llm.strategy;
import com.echoteam.MEC.common.LLMCode;
import com.echoteam.MEC.llm.client.LLMClient;
import com.echoteam.MEC.llm.configs.LlmModelProperties;
import com.echoteam.MEC.llm.support.PromptBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class DeepSeekStrategy extends AbstractLLMStrategy {
    private final LLMClient deepSeekClient;

    public DeepSeekStrategy(PromptBuilder promptBuilder,
                            LlmModelProperties props,
                            ObjectMapper mapper,
                            LLMClient deepSeekClient) {
        super(promptBuilder, props, mapper);
        this.deepSeekClient = deepSeekClient;
    }

    @Override
    protected LLMClient client() {
        return deepSeekClient;
    }

    @Override
    public LLMCode APIName() {
        return LLMCode.DEEPSEEK;
    }
}
