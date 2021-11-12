package com.github.euler.stt;

import java.io.IOException;

import org.vosk.Recognizer;

public interface VoskRecognizerLoader {

    Recognizer load() throws IOException;

}
