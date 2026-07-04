package com.FaceYourInterview.service;

import com.FaceYourInterview.model.InterviewSession;
import com.FaceYourInterview.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Autowired
    private InterviewSessionManager sessionManager;

    public Report generateReport(String sessionId) {

        InterviewSession session = sessionManager.get(sessionId);

        if (session == null) {
            throw new RuntimeException("Invalid sessionId: " + sessionId);
        }

        Report report = new Report();

        report.setTotalQuestions(
                session.getQuestions() != null ? session.getQuestions().size() : 0
        );

        report.setAnswers(session.getAnswers());
        report.setScore(session.getScore());

        return report;
    }
}