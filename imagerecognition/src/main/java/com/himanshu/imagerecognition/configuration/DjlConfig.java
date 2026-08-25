package com.himanshu.imagerecognition.configuration;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.CenterCrop;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;

@Configuration
public class DjlConfig {

    @Bean
    public ZooModel<Image, Classifications> imageModel() throws ModelException, IOException {
        Criteria<Image, Classifications> criteria = Criteria.builder()
        .setTypes(Image.class, Classifications.class)
        .optEngine("PyTorch")
        .optFilter("backbone", "resnet50")
        .optTranslator(ImageClassificationTranslator.builder()
            .addTransform(new Resize(256))
            .addTransform(new CenterCrop(224, 224))
            .addTransform(new ToTensor())
            .optApplySoftmax(true)
            .build())
        .build();

        return criteria.loadModel();
    }
}
