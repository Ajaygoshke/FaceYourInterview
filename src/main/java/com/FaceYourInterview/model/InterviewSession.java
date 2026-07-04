package com.FaceYourInterview.model;

import java.util.ArrayList;
import java.util.List;

public class InterviewSession {

    private String sessionId;
    private String resumeText;

    private int questionIndex = 0;
    public InterviewSession() {
		super();
	}

	public InterviewSession(String sessionId, String resumeText, int questionIndex, int score, List<String> questions,
			List<String> answers) {
		super();
		this.sessionId = sessionId;
		this.resumeText = resumeText;
		this.questionIndex = questionIndex;
		this.score = score;
		this.questions = questions;
		this.answers = answers;
	}

	@Override
	public String toString() {
		return "InterviewSession [sessionId=" + sessionId + ", resumeText=" + resumeText + ", questionIndex="
				+ questionIndex + ", score=" + score + ", questions=" + questions + ", answers=" + answers + "]";
	}

	private int score = 0;

    private List<String> questions = new ArrayList<>();
    private List<String> answers = new ArrayList<>();

    // getters and setters

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
}