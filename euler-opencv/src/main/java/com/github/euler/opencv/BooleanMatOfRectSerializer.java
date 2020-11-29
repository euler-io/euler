package com.github.euler.opencv;

import org.opencv.core.MatOfRect;

public class BooleanMatOfRectSerializer implements MatOfRectSerializer {

    @Override
    public Object serialize(MatOfRect rects) {
        return rects.total() > 0;
    }

}
