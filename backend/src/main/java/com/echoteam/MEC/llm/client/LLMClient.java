package com.echoteam.MEC.llm.client;

public interface LLMClient {

    String callLLM(String prompt, String model);
}
