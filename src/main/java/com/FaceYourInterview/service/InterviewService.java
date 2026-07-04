package com.FaceYourInterview.service;

import com.FaceYourInterview.Dto.InterviewConfigRequest;
import com.FaceYourInterview.Dto.InterviewResponse;
import com.FaceYourInterview.model.InterviewSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InterviewService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private InterviewSessionManager sessionManager;

    // ==============================
    // GENERATE BALANCED GENERIC QUESTIONS
    // ==============================
    public List<String> generateQuestions(String resume) {
        String prompt = """
        You are an expert technical interviewer assessing a software engineering candidate.
        Analyze the following resume thoroughly, paying close attention to the 'Skills', 'Technologies', 
        and 'Core Frameworks' sections, as well as their listed projects.
        
        Generate exactly 5 distinct interview questions tailored directly to this resume. 
        You must follow this exact breakdown structure:
        
        1. Question 1: Core Technical Theory / Deep-dive into their primary programming language.
        2. Question 2: Advanced Framework / Backend / Frontend architecture question based on their tech stack.
        3. Question 3: A scenario-based technical problem-solving question involving one of their core skills.
        4. Question 4: Project Deep-Dive: Ask about a specific architecture challenge in one of their projects.
        5. Question 5: Behavioral / Project Management: How they handled deployment, bugs, or collaboration.

        Resume Data:
        """ + resume + """

        Return ONLY the 5 questions in a clean numbered list format (e.g., 1., 2.). 
        Do not include introductory greetings, explanations, headings, or concluding remarks.
        """;

        try {
            String response = geminiService.generate(prompt);
            List<String> parsed = parseQuestions(response);
            
            if (parsed.isEmpty()) {
                return getDefaultQuestions();
            }
            return parsed;
        } catch (Exception e) {
            System.err.println("Error calling Gemini during question generation: " + e.getMessage());
            return getDefaultQuestions();
        }
    }

    // ==============================
    // DYNAMIC GENERATION BASED ON USER PREFERENCES
    // ==============================
    public List<String> generateCustomQuestions(String resume, String language, int count) {
        String prompt = String.format("""
        You are an expert technical interviewer assessing a software engineering candidate.
        
        The user has selected to be interviewed specifically on the programming language: %s.
        They want exactly %d questions.

        Analyze their resume, but focus primarily on assessing their depth of knowledge in %s.
        
        Generate exactly %d questions. Mix deep technical concepts, language features, and situational problem-solving based on %s.

        Candidate Resume Data for Context:
        %s

        Return ONLY the %d questions in a clean numbered list format (e.g., 1., 2.).
        Do not include introductory greetings, markdown bolding headers, or concluding remarks.
        """, language, count, language, count, language, resume, count);

        try {
            String response = geminiService.generate(prompt);
            List<String> parsed = parseQuestions(response);
            
            if (parsed.isEmpty()) {
                return getDefaultQuestions(count);
            }
            return parsed;
        } catch (Exception e) {
            System.err.println("Error calling Gemini during custom question generation: " + e.getMessage());
            return getDefaultQuestions(count);
        }
    }

    // ==============================
    // START DYNAMIC CONFIG INTERVIEW
    // ==============================
    public InterviewResponse startInterview(String sessionId, InterviewConfigRequest config) {
        InterviewSession session = sessionManager.get(sessionId);
        if (session == null) {
            throw new RuntimeException("Session not found: " + sessionId);
        }

        String resume = session.getResumeText();
        if (resume == null || resume.trim().isEmpty()) {
            resume = "Standard Developer Profile";
        }

        List<String> questions = generateCustomQuestions(
                resume, 
                config.getSelectedLanguage(), 
                config.getTotalQuestions()
        );
        
        session.setQuestions(questions);
        session.setQuestionIndex(0);
        session.setScore(0);
        session.setAnswers(new ArrayList<>());

        InterviewResponse response = new InterviewResponse();
        response.setQuestion(questions.get(0));
        return response;
    }

    // ==============================
    // EVALUATE USER CONTINUOUS ANSWER
    // ==============================
    public InterviewResponse evaluateAnswer(String sessionId, String answer) {
        InterviewSession session = sessionManager.get(sessionId);
        if (session == null) {
            return new InterviewResponse("System error: Session missing. Please refresh.");
        }

        List<String> questions = session.getQuestions();
        if (questions == null || questions.isEmpty()) {
            return new InterviewResponse("Please wait while your session question pool initializes.");
        }

        int index = session.getQuestionIndex();
        if (index >= questions.size()) {
            return new InterviewResponse("Interview Completed. Thank you for your time.");
        }

        String question = questions.get(index);

        String prompt = """
        Evaluate this interview answer.
        Question: """ + question + """
        Answer: """ + answer + """

        Your response must strictly follow this format structure:
        RESULT: [PASS or FAIL]
        FEEDBACK: [Your short conversational feedback here]
        
        Criteria for PASS: The answer makes structural sense or is accurate.
        """;

        String evaluation = "";
        try {
            String aiResponse = geminiService.generate(prompt);
            if (aiResponse != null) {
                evaluation = aiResponse;
            } else {
                evaluation = "RESULT: PASS \nFEEDBACK: Good effort on the response.";
            }
        } catch (Exception e) {
            System.err.println("Gemini evaluation error fallback: " + e.getMessage());
            evaluation = "RESULT: PASS \nFEEDBACK: Response processed successfully.";
        }

        if (session.getAnswers() == null) {
            session.setAnswers(new ArrayList<>());
        }
        session.getAnswers().add(answer);

        if (evaluation.contains("RESULT: PASS")) {
            session.setScore(session.getScore() + 1);
        }

        String feedback = evaluation.replace("RESULT: PASS", "")
                                    .replace("RESULT: FAIL", "")
                                    .replace("FEEDBACK:", "")
                                    .trim();

        int nextIndex = index + 1;
        session.setQuestionIndex(nextIndex);

        if (nextIndex < questions.size()) {
            return new InterviewResponse(questions.get(nextIndex));
        } else {
            return new InterviewResponse("Interview Completed. Final Score: " + session.getScore() + "/" + questions.size() + ". Feedback: " + feedback);
        }
    }

    // ==============================
    // PARSING TEXT CLEANER UTILITIES
    // ==============================
    private List<String> parseQuestions(String response) {
        List<String> questions = new ArrayList<>();
        if (response == null || response.trim().isEmpty()) return questions;

        String[] lines = response.split("\n");
        for (String line : lines) {
            line = line.replaceAll("^\\d+\\.\\s*", "").trim();
            line = line.replaceAll("\\*", "").trim();
            if (!line.isEmpty() && line.length() > 5) {
                questions.add(line);
            }
        }
        return questions;
    }

    // Standard fallback list
    private List<String> getDefaultQuestions() {
        return getDefaultQuestions(5);
    }

    // Dynamic length tracking fallback generator
    private List<String> getDefaultQuestions(int count) {
        List<String> defaults = new ArrayList<>();
        defaults.add("Can you explain the memory management model of your preferred programming language?");
        defaults.add("How do you design code architectures to ensure optimal multi-threading performance?");
        defaults.add("What are your primary strategies for tracking and resolving unexpected runtime exceptions?");
        defaults.add("Can you contrast the architectural pros and cons of microservices versus a monolithic layout?");
        defaults.add("How do you manage breaking structural changes in production databases without causing user downtime?");
        defaults.add("Describe your testing workflow to ensure regressions aren't deployed to manufacturing frameworks.");
        defaults.add("What are the primary differences between SQL scaling and NoSQL structural storage models?");
        
        // If user asks for 10 questions, but default pool has 7, it won't crash with IndexOutOfBounds
        int maxAvailable = Math.min(count, defaults.size());
        return new ArrayList<>(defaults.subList(0, maxAvailable));
    }
}