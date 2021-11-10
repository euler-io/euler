package com.github.euler.barcode;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.zxing.Result;

public class DefaultBarcodeResultSerializer implements BarcodeResultSerializer {

    @Override
    public Object serialize(List<Result> results) {
        return results.stream()
                .map(r -> toMap(r))
                .collect(Collectors.toList());
    }

    private Map<String, Object> toMap(Result r) {
        return Map.of(
                "text", r.getText(),
                "format", r.getBarcodeFormat().toString(),
                "timestamp", new Date(r.getTimestamp()));
    }

}
