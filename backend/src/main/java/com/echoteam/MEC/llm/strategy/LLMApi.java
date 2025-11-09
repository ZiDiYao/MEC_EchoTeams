package com.echoteam.MEC.llm.strategy;

import com.echoteam.MEC.common.LLMCode;
import com.echoteam.MEC.dto.AnalysisResponse;
import com.echoteam.MEC.dto.NormalizedAnalysisRequest;

public interface LLMApi {
    // Provider enum (CHATGPT / DEEPSEEK)
    LLMCode APIName();

    // Main entry of strategy. Builds prompt, calls client, parses JSON, normalizes result.
    AnalysisResponse handle(NormalizedAnalysisRequest request);
}