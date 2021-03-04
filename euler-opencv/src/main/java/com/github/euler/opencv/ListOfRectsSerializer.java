package com.github.euler.opencv;

import org.opencv.core.MatOfRect;

public class ListOfRectsSerializer implements MatOfRectSerializer {

    @Override
    public Object serialize(MatOfRect rects) {
        return rects.toList();
    }

}
