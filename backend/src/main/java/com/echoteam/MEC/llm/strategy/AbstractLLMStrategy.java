package com.echoteam.MEC.llm.strategy;
import com.echoteam.MEC.common.LLMCode;
import com.echoteam.MEC.dto.AnalysisResponse;
import com.echoteam.MEC.dto.NormalizedAnalysisRequest;
import com.echoteam.MEC.llm.client.LLMClient;
import com.echoteam.MEC.llm.configs.LlmModelProperties;
import com.echoteam.MEC.llm.configs.LlmModelProperties.ModelConfig;
import com.echoteam.MEC.llm.support.PromptBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
public abstract class AbstractLLMStrategy implements LLMApi {

    protected final PromptBuilder promptBuilder;
    protected final LlmModelProperties modelProps;
    protected final ObjectMapper mapper;

    @Autowired
    protected AbstractLLMStrategy(PromptBuilder promptBuilder,
                                  LlmModelProperties modelProps,
                                  ObjectMapper mapper) {
        this.promptBuilder = promptBuilder;
        this.modelProps = modelProps;
        this.mapper = mapper;
    }

    protected abstract LLMClient client();

    @Override
    public abstract LLMCode APIName();

    @Override
    public AnalysisResponse handle(NormalizedAnalysisRequest request) {
        String prompt = promptBuilder.buildPrompt(request);
        String realModel = resolveRealModelName();

        String raw = client().callLLM(prompt, realModel);
        AnalysisResponse resp = parseToResponse(raw);

        normalize(request, resp);
        return resp;
    }

    protected String resolveRealModelName() {
        String key = APIName().name();
        ModelConfig cfg = (modelProps.getModels() != null)
                ? modelProps.getModels().get(key)
                : null;

        if (cfg == null) {
            cfg = modelProps.getActiveModelConfig();
        }
        if (cfg == null || isBlank(cfg.getModelName())) {
            throw new IllegalStateException("No model-name configured for: " + key);
        }
        return cfg.getModelName();
    }

    protected AnalysisResponse parseToResponse(String raw) {
        try {
            return mapper.readValue(raw, AnalysisResponse.class);
        } catch (Exception first) {
            String jsonOnly = extractJsonSafely(raw);
            try {
                return mapper.readValue(jsonOnly, AnalysisResponse.class);
            } catch (Exception second) {
                throw new RuntimeException("LLM JSON parse failed: " + second.getMessage() +
                        " raw=" + shorten(raw, 400), second);
            }
        }
    }

    protected String extractJsonSafely(String s) {
        if (s == null) return "{}";
        int l = s.indexOf('{');
        int r = s.lastIndexOf('}');
        if (l >= 0 && r > l) return s.substring(l, r + 1);
        return "{}";
    }

    protected void normalize(NormalizedAnalysisRequest req, AnalysisResponse r) {
        if (isBlank(r.getRequestId()))         r.setRequestId(req.getRequestId());
        if (isBlank(r.getPhoneNumber()))       r.setPhoneNumber(req.getPhoneNumber());
        if (r.getTimeReported() == null)       r.setTimeReported(req.getTimeReported());
        if (isBlank(r.getCallerName()))        r.setCallerName("UNKNOWN");
        if (isBlank(r.getIncidentType()))        r.setIncidentType("UNKNOWN");
        if (isBlank(r.getIncidentDescription())) r.setIncidentDescription("UNKNOWN");
        if (isBlank(r.getAddress()))             r.setAddress("UNKNOWN");
        if (isBlank(r.getLandmark()))            r.setLandmark("UNKNOWN");
        if (r.getVictimCount() == null)          r.setVictimCount(0);
        if (isBlank(r.getVictimDescription()))   r.setVictimDescription("UNKNOWN");
        if (isBlank(r.getInjurySeverity()))      r.setInjurySeverity("Unknown");
        if (r.getIsConscious() == null) r.setIsConscious(false);
        if (r.getIsBreathing() == null) r.setIsBreathing(false);
        if (r.getIsOngoing() == null)   r.setIsOngoing(false);
        if (r.getIsFire() == null)      r.setIsFire(false);

        if (r.getUrgencyLevel() == null) r.setUrgencyLevel(50);
        else                             r.setUrgencyLevel(clampInt(r.getUrgencyLevel(), 1, 100));

        if (r.getKeyPhrases() == null) r.setKeyPhrases(java.util.Collections.emptyList());
        r.setConfidence(clampInt(r.getConfidence(), 0, 100));
        if (isBlank(r.getProcessedBy())) r.setProcessedBy(APIName().name());
    }

    protected static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    protected static int clampInt(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }
    protected static String shorten(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
