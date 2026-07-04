package com.FaceYourInterview.controller;

import com.FaceYourInterview.Dto.InterviewConfigRequest;
import com.FaceYourInterview.Dto.InterviewRequest;
import com.FaceYourInterview.Dto.InterviewResponse;
import com.FaceYourInterview.Dto.ResumeRequest;
import com.FaceYourInterview.service.InterviewService;
import com.FaceYourInterview.service.InterviewSessionManager;
import com.FaceYourInterview.service.ReportService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = "http://localhost:5173") // Perfectly matches your Vite React default port!
public class InterviewController {

    private final InterviewService interviewService;
    private final InterviewSessionManager sessionManager;
    private final ReportService reportService;

    public InterviewController(
            InterviewService interviewService,
            InterviewSessionManager sessionManager,
            ReportService reportService
    ) {
        this.interviewService = interviewService;
        this.sessionManager = sessionManager;
        this.reportService = reportService;
    }

    // 1. CREATES SESSION & EXTRACTS RESUME
    @PostMapping("/session")
    public String createSession(@RequestBody ResumeRequest request) {
        return sessionManager.createSession(request.getResumeText());
    }

    // 2. FIXED: Now actually triggers the service to analyze the resume and return question 1!
    @PostMapping("/start")
    public ResponseEntity<InterviewResponse> startInterview(
            @RequestParam String sessionId, 
            @RequestBody InterviewConfigRequest config) {
        
        InterviewResponse response = interviewService.startInterview(sessionId, config);
        return ResponseEntity.ok(response);
    }
    // 3. HANDLES EVALUATION & PROGRESSION
    @PostMapping("/answer")
    public InterviewResponse answer(
            @RequestParam String sessionId,
            @RequestBody InterviewRequest request
    ) {
        return interviewService.evaluateAnswer(sessionId, request.getAnswer());
    }

    // 4. GENERATES FINAL SCORE SHEET / INTERVIEW REPORT
    @GetMapping("/report")
    public Object getReport(@RequestParam String sessionId) {
        return reportService.generateReport(sessionId);
    }
}