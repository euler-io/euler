package com.github.euler.opencv;

import org.opencv.core.MatOfRect;

public interface MatOfRectSerializer {

    Object serialize(MatOfRect rects);

}
