package com.FaceYourInterview.controller;



import com.FaceYourInterview.model.Resume;
import com.FaceYourInterview.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "https://face-your-interview.vercel.app")
public class ResumeController {

    private final ResumeService service;

    public ResumeController(ResumeService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<Resume> upload(
            @RequestParam String title,
            @RequestParam String role,
            @RequestParam MultipartFile file
    ) {
        return ResponseEntity.ok(service.uploadResume(title, role, file));
    }

    @GetMapping
    public ResponseEntity<List<Resume>> getAll() {
        return ResponseEntity.ok(service.getAllResumes());
    }
}