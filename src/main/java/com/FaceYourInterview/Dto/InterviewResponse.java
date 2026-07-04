package com.FaceYourInterview.Dto;

public class InterviewResponse {

    private String question;
    private String feedback;
    private int score;
    private String correctness;

    public InterviewResponse() {}

    public InterviewResponse(String question) {
        this.question = question;
    }


	public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getCorrectness() {
        return correctness;
    }

    public void setCorrectness(String correctness) {
        this.correctness = correctness;
    }

	public void setSessionId(String sessionId) {
		// TODO Auto-generated method stub
		
	}
}