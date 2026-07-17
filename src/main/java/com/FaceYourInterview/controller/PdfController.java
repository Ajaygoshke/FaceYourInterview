package com.FaceYourInterview.controller;

import com.FaceYourInterview.model.GeminiResponse;
import com.FaceYourInterview.service.GeminiService;
import com.FaceYourInterview.service.PdfService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "https://face-your-interview.vercel.app/")
public class PdfController {

    private final PdfService pdfService;
    private final GeminiService geminiService;

    public PdfController(PdfService pdfService,
                         GeminiService geminiService) {
        this.pdfService = pdfService;
        this.geminiService = geminiService;
    }

    @PostMapping("/analyze")
    public GeminiResponse analyzePdf(
            @RequestParam("file") MultipartFile file) {

        String text = pdfService.extractText(file);

        String result = geminiService.analyzeResume(text);

        return new GeminiResponse(result);
    }
}