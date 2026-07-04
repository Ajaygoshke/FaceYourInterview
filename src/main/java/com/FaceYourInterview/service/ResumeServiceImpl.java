package com.FaceYourInterview.service;

import com.FaceYourInterview.Repositry.ResumeRepository;
import com.FaceYourInterview.model.Resume;
import com.FaceYourInterview.service.ResumeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository repository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ResumeServiceImpl(ResumeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Resume uploadResume(String title, String role, MultipartFile file) {

        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);

            Files.write(path, file.getBytes());

            String fileUrl = "/uploads/" + fileName;

            Resume resume = new Resume(title, role, fileUrl);
            return repository.save(resume);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    @Override
    public List<Resume> getAllResumes() {
        return repository.findAll();
    }
}