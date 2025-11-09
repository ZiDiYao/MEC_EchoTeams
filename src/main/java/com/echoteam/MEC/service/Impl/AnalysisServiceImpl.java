package com.echoteam.MEC.service.Impl;

import com.echoteam.MEC.dto.AnalysisRequest;
import com.echoteam.MEC.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/api/analysisService")
@RequiredArgsConstructor
public class AnalysisServiceImpl {


    public AnalysisResponse analyze(@RequestBody AnalysisRequest request){


        return null;

    }


}
