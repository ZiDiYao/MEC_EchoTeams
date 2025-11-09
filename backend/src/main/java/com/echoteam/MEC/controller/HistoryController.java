package com.echoteam.MEC.controller;

import com.echoteam.MEC.domain.AnalysisRecord;
import com.echoteam.MEC.service.HistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/history", produces = MediaType.APPLICATION_JSON_VALUE)
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public Page<AnalysisRecord> getHistory(
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        if (phone == null || phone.isBlank()) {
            // select all
            return historyService.findAll(pageable);
        } else {
            // Select by phone Number
            return historyService.historyByPhone(phone, pageable);
        }
    }

    @GetMapping("/{requestId}")
    public AnalysisRecord getByRequestId(@PathVariable String requestId) {
        return historyService.getByRequestId(requestId);
    }
}
