package com.echoteam.MEC.llm.clients;

public interface LLMClient {
    String callLLM(String prompt, String model);
}
