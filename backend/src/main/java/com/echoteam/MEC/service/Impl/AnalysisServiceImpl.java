package com.echoteam.MEC.service.Impl;
import com.echoteam.MEC.dto.AnalysisRequest;
import com.echoteam.MEC.dto.AnalysisResponse;
import com.echoteam.MEC.dto.NormalizedAnalysisRequest;
import com.echoteam.MEC.factories.LLMApiFactory;
import com.echoteam.MEC.llm.configs.LlmModelProperties;
import com.echoteam.MEC.llm.strategy.LLMApi;
import com.echoteam.MEC.service.AnalysisService;
import com.echoteam.MEC.service.HistoryService;
import com.echoteam.MEC.service.ProcessingRawDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final ProcessingRawDataService normalizer;
    private final LLMApiFactory llmFactory;
    private final LlmModelProperties llmProps;
    private final HistoryService historyService;

    public AnalysisServiceImpl(ProcessingRawDataService normalizer,
                               LLMApiFactory llmFactory,
                               LlmModelProperties llmProps,
                               HistoryService historyService) {
        this.normalizer = normalizer;
        this.llmFactory = llmFactory;
        this.llmProps = llmProps;
        this.historyService = historyService;
    }

    @Override
    @Transactional
    public AnalysisResponse analyze(AnalysisRequest req, String providerParam, String providerHeader) {

        NormalizedAnalysisRequest normalized = normalizer.normalize(req);
        String chosen = firstNonBlank(providerHeader, providerParam, llmProps.getActive());
        if (isBlank(chosen)) {
            throw new IllegalArgumentException("No LLM provider specified and mec.llm.active is empty.");
        }

        LLMApi api = llmFactory.getLLMApi(chosen);
        AnalysisResponse result = api.handle(normalized);

        if (isBlank(result.getRequestId()))   result.setRequestId(normalized.getRequestId());
        if (isBlank(result.getPhoneNumber())) result.setPhoneNumber(normalized.getPhoneNumber());
        if (result.getTimeReported() == null) result.setTimeReported(normalized.getTimeReported());

        historyService.saveFromResponse(result);
        return result;
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String firstNonBlank(String... vals) {
        for (String v : vals) if (!isBlank(v)) return v.trim();
        return null;
    }
}
