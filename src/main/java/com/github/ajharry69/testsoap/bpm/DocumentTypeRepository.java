package com.github.ajharry69.testsoap.bpm;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class DocumentTypeRepository {

    public boolean existsByDocumentNameAndActiveTrue(String value) {
        return new Random().nextBoolean();
    }
}
