
package com.echoteam.MEC.llm.configs;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "mec.llm")
public class LlmModelProperties {


    private String active; // The model that is currently using

    private Map<String, ModelConfig> models;

    public static class ModelConfig {
        private String apiUrl;
        private String apiKey;
        private String modelName;
        private double temperature = 0.0;

        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }

        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
    }

    public String getActive() { return active; }
    public void setActive(String active) { this.active = active; }

    public Map<String, ModelConfig> getModels() { return models; }
    public void setModels(Map<String, ModelConfig> models) { this.models = models; }

    public ModelConfig getActiveModelConfig() {
        if (models == null || !models.containsKey(active)) {
            throw new IllegalStateException("No LLM model found for key: " + active);
        }
        return models.get(active);
    }

}
