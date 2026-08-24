package com.himanshu.async.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Async("taskExecutor") // Specify the executor to be used for asynchronous processing
    public void generateAuditLog(String action) {
        // Simulate generating an audit log for the given action
        System.out.println("Generating audit log for action: " + action);
        try {
            // Simulate a delay to represent the time taken to generate the audit log
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Handle the InterruptedException if the thread is interrupted during sleep
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.err.println("Audit log generation was interrupted: " + e.getMessage());
        }
        System.out.println("Audit log generated for action: " + action + " by thread: " + Thread.currentThread().getName());
    }

    @Async("taskExecutor") // Specify the executor to be used for asynchronous processing
    public CompletableFuture<String> buildPdfReporting(String reportName) {
        // Simulate building a PDF report for the given report name
        System.out.println("Building PDF report: " + reportName);
        try {
            // Simulate a delay to represent the time taken to build the PDF report
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // Handle the InterruptedException if the thread is interrupted during sleep
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.err.println("PDF report generation was interrupted: " + e.getMessage());
        }
        System.out.println("PDF report built: " + reportName + " by thread: " + Thread.currentThread().getName());
        return CompletableFuture.completedFuture(reportName + ".pdf report generated successfully");
    }
}
