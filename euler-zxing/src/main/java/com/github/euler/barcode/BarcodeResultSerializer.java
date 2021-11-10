package com.github.euler.barcode;

import java.util.List;

import com.google.zxing.Result;

public interface BarcodeResultSerializer {

    Object serialize(List<Result> results);

}
