package com.FaceYourInterview.service;

import com.FaceYourInterview.model.InterviewSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class InterviewSessionManager {

    private final Map<String, InterviewSession> sessions = new HashMap<>();

    // CREATE SESSION
    public String createSession(String resumeText) {

        String sessionId = UUID.randomUUID().toString();

        InterviewSession session = new InterviewSession();
        session.setSessionId(sessionId);
        session.setResumeText(resumeText);

        sessions.put(sessionId, session);

        return sessionId;
    }

    // GET SESSION
    public InterviewSession get(String sessionId) {
        return sessions.get(sessionId);
    }
}