package com.himanshu.async.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.himanshu.async.service.ReportService;

@RestController
@RequestMapping("/api/v1/reports") // Base URL for report-related endpoints
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Trigger fire and forget background task (Responds immediately)
    @PostMapping("/audit")
    public ResponseEntity<String> triggerAuditLog(@RequestParam String action) {
        System.out.println("Received request to generate audit log for action: " + action + " by thread: " + Thread.currentThread().getName());
        reportService.generateAuditLog(action);
        System.out.println("Audit log generation request submitted for action: " + action + " by thread: " + Thread.currentThread().getName());
        return ResponseEntity.ok("Audit log generation request submitted for action: " + action);
    }

    // Triggers async process and return completablefuture
    @GetMapping("/generate")
    public CompletableFuture<ResponseEntity<String>> generateReport(@RequestParam String name){
        System.out.println("Received PDF request "+name+" by thread: "+Thread.currentThread().getName());
        return reportService.buildPdfReporting(name)
                    .thenApply(result -> {
                        System.out.println(Thread.currentThread().getName()+" sending http response back.");
                        return ResponseEntity.ok(result);
                    });
    }
}
