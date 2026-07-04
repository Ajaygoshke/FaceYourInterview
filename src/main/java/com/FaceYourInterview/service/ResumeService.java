package com.FaceYourInterview.service;

import com.FaceYourInterview.model.Resume;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {

    Resume uploadResume(String title, String role, MultipartFile file);

    List<Resume> getAllResumes();
}