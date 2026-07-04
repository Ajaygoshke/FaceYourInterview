package com.FaceYourInterview.Dto;


public class InterviewRequest {

    private String answer;

    public InterviewRequest() {
    }

    public InterviewRequest(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}