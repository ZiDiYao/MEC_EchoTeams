package com.echoteam.MEC.llm.support;

import com.echoteam.MEC.dto.NormalizedAnalysisRequest;

public interface PromptBuilder {

    String buildPrompt(NormalizedAnalysisRequest request);
}
