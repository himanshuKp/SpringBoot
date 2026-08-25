package com.himanshu.imagerecognition.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.himanshu.imagerecognition.dto.PredictionResponse;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.repository.zoo.ZooModel;

@Service
public class VisionService {
    private final ZooModel<Image, Classifications> model;

    public VisionService(ZooModel<Image, Classifications> model) {
        this.model = model;
    }

    public List<PredictionResponse> recognize(MultipartFile file) {
        try (InputStream is = file.getInputStream();
                Predictor<Image, Classifications> predictor = model.newPredictor()) {
            Image image = ImageFactory.getInstance().fromInputStream(is);
            Classifications classifications = predictor.predict(image);

            return classifications.topK(5).stream()
                    .map(item -> new PredictionResponse(item.getClassName(), item.getProbability()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error processing image recognition: ", e);
        }
    }
}
