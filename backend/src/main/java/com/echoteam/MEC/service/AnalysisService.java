package com.echoteam.MEC.service;
import com.echoteam.MEC.dto.AnalysisRequest;
import com.echoteam.MEC.dto.AnalysisResponse;


public interface AnalysisService {
    AnalysisResponse analyze(AnalysisRequest req, String providerParam, String providerHeader);
}
