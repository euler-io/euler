package com.github.euler.opencv;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class MatUtils {

    public static Mat decode(InputStream in, int flags) throws IOException {
        byte[] bytes = IOUtils.toByteArray(in);
        return Imgcodecs.imdecode(new MatOfByte(bytes), flags);
    }

}
