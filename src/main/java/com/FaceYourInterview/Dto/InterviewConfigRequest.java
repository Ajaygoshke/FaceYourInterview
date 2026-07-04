package com.FaceYourInterview.Dto;

public class InterviewConfigRequest {
    private int totalQuestions;
    private String selectedLanguage;
	public int getTotalQuestions() {
		return totalQuestions;
	}
	public void setTotalQuestions(int totalQuestions) {
		this.totalQuestions = totalQuestions;
	}
	public String getSelectedLanguage() {
		return selectedLanguage;
	}
	public void setSelectedLanguage(String selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}
    
}