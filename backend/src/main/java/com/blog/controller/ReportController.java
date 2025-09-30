package com.blog.controller;

import com.blog.service.ReportService;
import com.blog.dto.ReportRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import com.blog.dto.ReportProfileResponse;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/profile/{uuid}")
    public ResponseEntity<ReportProfileResponse> reportProfile(
            @PathVariable String uuid,
            @RequestBody ReportRequest reason) {
        ReportProfileResponse res = reportService.reportProfile(uuid, reason);
        return ResponseEntity.ok(res);
    }
}
