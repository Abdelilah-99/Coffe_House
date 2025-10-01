package com.blog.controller;

import com.blog.service.ReportService;
import com.blog.dto.ReportRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import com.blog.dto.ReportResponse;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/profile/{uuid}")
    public ResponseEntity<ReportResponse> reportProfile(
            @PathVariable String uuid,
            @RequestBody ReportRequest reason) {
        ReportResponse res = reportService.reportProfile(uuid, reason);
        return ResponseEntity.ok(res);
    }

    @PostMapping("post/{uuid}")
    public ResponseEntity<ReportResponse> reportPost(
            @PathVariable String uuid,
            @RequestBody ReportRequest reason) {
        ReportResponse res = reportService.reportPost(uuid, reason);
        return ResponseEntity.ok(res);
    }
}
