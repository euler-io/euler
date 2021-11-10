package com.github.euler.video;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.euler.common.StorageStrategy;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;
import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.BaseInput;
import com.github.kokorin.jaffree.ffmpeg.BaseOutput;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;

public class FastFFmpegVideoThumbnailItemProcessor implements ItemProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final InputFactory inputFactory;
    private final OutputFactory outputFactory;
    private final StorageStrategy storageStrategy;
    private final int width;
    private final int height;
    private final float position;
    private final String field;
    private final String[] additionalArgs;

    public FastFFmpegVideoThumbnailItemProcessor(InputFactory inputFactory,
            OutputFactory outputFactory,
            StorageStrategy storageStrategy,
            int width,
            int height,
            float position,
            String field,
            String... additionalArgs) {
        super();
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.storageStrategy = storageStrategy;
        this.width = width;
        this.height = height;
        this.position = position;
        this.field = field;
        this.additionalArgs = additionalArgs;

        if (position < 0f || position > 1f) {
            throw new IllegalArgumentException("position must be between 0 and 1");
        }
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        try {
            URI outURI = storageStrategy.createFile(item.itemURI);
            generatePreview(item.itemURI, outURI, item.ctx);
            return ProcessingContext.builder()
                    .metadata(field, outURI.toString())
                    .build();
        } catch (Throwable e) {
            LOGGER.warn("An error ocurred while creating video preview for " + item.itemURI, e);
            return ProcessingContext.EMPTY;
        }
    }

    private void generatePreview(URI inURI, URI outURI, ProcessingContext ctx) throws IOException {
        Float durationInMilis = getDurationInMilis(inputFactory.createFFprobe(inURI, ctx));
        long framePosition = Float.valueOf(durationInMilis.floatValue() * position).longValue();
        generate(framePosition, inputFactory.createFFmpeg(inURI, ctx), outputFactory.create(outURI, ctx));
    }

    private void generate(long framePosition, BaseInput<?> input, BaseOutput<?> output) {
        FFmpeg ffmpeg = FFmpeg.atPath()
                .setLogLevel(LogLevel.QUIET)
                .setOverwriteOutput(true);

        Arrays.stream(additionalArgs).forEach(a -> ffmpeg.addArgument(a));

        input.setPosition(framePosition);
        output.setFrameCount(StreamType.VIDEO, 1l)
                .addArguments("-vf", buildFilter());
        ffmpeg.addInput(input).addOutput(output);
        ffmpeg.execute();
    }

    private Float getDurationInMilis(com.github.kokorin.jaffree.ffprobe.Input in) throws IOException {
        FFprobeResult result = FFprobe.atPath()
                .setShowStreams(true)
                .setLogLevel(LogLevel.QUIET)
                .setShowEntries("format=duration")
                .setShowPrivateData(false)
                .setShowStreams(false)
                .setInput(in)
                .execute();
        return result.getFormat().getDuration() * 1000f;
    }

    private String buildFilter() {
        return "scale=iw*min(" + width + "/iw\\," + height + "/ih):ih*min(" + width + "/iw\\," + height + "/ih):flags=lanczos, pad=" + width + ":" + height + ":("
                + width + "-iw*min("
                + width + "/iw\\," + height + "/ih))/2:(" + height + "-ih*min(" + width + "/iw\\," + height + "/ih))/2, unsharp=5:5:0.5:5:5:0.5";
    }

    public static void main(String[] args) throws Exception {
        URLInputOutputFactory ioFactory = new URLInputOutputFactory();
        FastFFmpegVideoThumbnailItemProcessor itemProcessor = new FastFFmpegVideoThumbnailItemProcessor(ioFactory, ioFactory, null, 320, 240, .5f, "video-thumbnail");
        URI in = new URI("file:///media/dell/storage/AquaTeen_O_Espirito_Cibernetico_do_Natal_Passado.avi");
        URI out = new URI("file:///media/dell/storage/AquaTeen_O_Espirito_Cibernetico_do_Natal_Passado.png");
        itemProcessor.generatePreview(in, out, ProcessingContext.EMPTY);
    }

}
