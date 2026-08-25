package com.himanshu.imagerecognition.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.himanshu.imagerecognition.dto.PredictionResponse;
import com.himanshu.imagerecognition.services.VisionService;

@RestController
@RequestMapping("/api/v1/vision")
public class VisionController {

    private final VisionService visionService;

    public VisionController(VisionService visionService) {
        this.visionService = visionService;
    }

    @PostMapping(value = "/classify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<PredictionResponse>> classifyImage(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(visionService.recognize(file));
    }

    @PostMapping(value = "/classify", consumes = "image/*")
    public ResponseEntity<List<PredictionResponse>> classifyRawImage(@RequestBody byte[] imageBytes) {
        if (imageBytes.length == 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(visionService.recognize(imageBytes));
    }
}
