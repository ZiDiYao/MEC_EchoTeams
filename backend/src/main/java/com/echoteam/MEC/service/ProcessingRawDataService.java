package com.echoteam.MEC.service;

import com.echoteam.MEC.dto.AnalysisRequest;
import com.echoteam.MEC.dto.NormalizedAnalysisRequest;

public interface ProcessingRawDataService {

    NormalizedAnalysisRequest normalize(AnalysisRequest req);

}