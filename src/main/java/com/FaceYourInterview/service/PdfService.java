package com.FaceYourInterview.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfService {

    // Stores the latest uploaded resume text
    private String currentResume;

    public String extractText(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();

            // Read only first 4 pages
            stripper.setStartPage(1);
            stripper.setEndPage(Math.min(4, document.getNumberOfPages()));

            String resumeText = stripper.getText(document);

            // Save resume text for Live Interview
            this.currentResume = resumeText;

            return resumeText;

        } catch (Exception e) {

            throw new RuntimeException("Error reading PDF: " + e.getMessage(), e);

        }

    }

    // Getter
    public String getCurrentResume() {
        return currentResume;
    }

}