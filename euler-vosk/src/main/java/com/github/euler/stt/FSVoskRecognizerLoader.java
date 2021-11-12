package com.github.euler.stt;

import java.io.IOException;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.SpeakerModel;

public class FSVoskRecognizerLoader implements VoskRecognizerLoader {

    private final String modelPath;
    private final float sampleRate;
    private final String speakerModelPath;

    public FSVoskRecognizerLoader(String modelPath, float sampleRate, String speakerModelPath) {
        super();
        this.modelPath = modelPath;
        this.sampleRate = sampleRate;
        this.speakerModelPath = speakerModelPath;
    }

    @Override
    public Recognizer load() throws IOException {
        Model model = new Model(modelPath);
        if (speakerModelPath != null) {
            SpeakerModel spkModel = new SpeakerModel(speakerModelPath);
            return new Recognizer(model, sampleRate, spkModel);
        } else {
            return new Recognizer(model, sampleRate);
        }
    }

}
