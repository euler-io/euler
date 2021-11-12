package com.github.euler.barcode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessingContext.Action;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;

/**
 * 
 * Based on
 * https://github.com/zxing/zxing/blob/master/zxingorg/src/main/java/com/google/zxing/web/DecodeServlet.java
 *
 */
public class BarcodeItemProcessor implements ItemProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final StreamFactory sf;
    private final String field;
    private final BarcodeResultSerializer serializer;

    private final Map<DecodeHintType, Object> hints;
    private final Map<DecodeHintType, Object> hintsPure;

    public BarcodeItemProcessor(StreamFactory sf, String field, BarcodeResultSerializer serializer, BarcodeFormat... formats) {
        super();
        this.sf = sf;
        this.field = field;
        this.serializer = serializer;

        this.hints = new EnumMap<>(DecodeHintType.class);
        this.hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        this.hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.copyOf(Arrays.asList(formats)));

        this.hintsPure = new EnumMap<>(hints);
        this.hintsPure.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        this.hintsPure.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.copyOf(Arrays.asList(formats)));
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        try (InputStream in = sf.openInputStream(item.itemURI, item.ctx)) {
            BufferedImage bim = ImageIO.read(in);
            List<Result> results = processImage(bim);
            return ProcessingContext.builder()
                    .metadata(field, serializer.serialize(results))
                    .setAction(Action.MERGE)
                    .build();
        } catch (Exception e) {
            LOGGER.warn("An error ocurred while detecting barcodes for " + item.itemURI, e);
            return ProcessingContext.EMPTY;
        }
    }

    public List<Result> processImage(BufferedImage bim) {
        List<Result> results = new ArrayList<>();

        LuminanceSource source = new BufferedImageLuminanceSource(bim);
        BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
        Reader reader = new MultiFormatReader();

        results.addAll(detectMultiple(bitmap, reader));

        if (results.isEmpty()) {
            // Look for pure barcode
            results.addAll(detect(bitmap, reader, hintsPure));
        }

        if (results.isEmpty()) {
            // Look for normal barcode in photo
            results.addAll(detect(bitmap, reader, hints));
        }

        if (results.isEmpty()) {
            // Try again with other binarizer
            BinaryBitmap hybridBitmap = new BinaryBitmap(new HybridBinarizer(source));
            results.addAll(detect(hybridBitmap, reader, hints));
        }

        return results;
    }

    private List<Result> detect(BinaryBitmap bitmap, Reader reader, Map<DecodeHintType, ?> hints) {
        try {
            Result result = reader.decode(bitmap, hints);
            if (result != null) {
                return List.of(result);
            } else {
                return List.of();
            }
        } catch (ReaderException e) {
            return List.of();
        }
    }

    private List<Result> detectMultiple(BinaryBitmap bitmap, Reader reader) {
        try {
            MultipleBarcodeReader multiReader = new GenericMultipleBarcodeReader(reader);
            Result[] results = multiReader.decodeMultiple(bitmap, hints);
            return Arrays.asList(results);
        } catch (ReaderException e) {
            return List.of();
        }
    }

}
