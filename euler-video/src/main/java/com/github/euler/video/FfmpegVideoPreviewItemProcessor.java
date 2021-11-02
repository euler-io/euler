package com.github.euler.video;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
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
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;

public class FfmpegVideoPreviewItemProcessor implements ItemProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final InputFactory inputFactory;
    private final OutputFactory outputFactory;
    private final StorageStrategy storageStrategy;
    private final int width;
    private final int height;
    private final String field;
    private final String[] additionalArgs;

    public FfmpegVideoPreviewItemProcessor(InputFactory inputFactory,
            OutputFactory outputFactory,
            StorageStrategy storageStrategy,
            int width,
            int height,
            String field,
            String... additionalArgs) {
        super();
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.storageStrategy = storageStrategy;
        this.width = width;
        this.height = height;
        this.field = field;
        this.additionalArgs = additionalArgs;
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        URI outURI = storageStrategy.createFile(item.itemURI, "_preview.mp4");
        try {
            generatePreview(item.itemURI, outURI, item.ctx);
        } catch (Throwable e) {
            LOGGER.warn("An error ocurred while creating video preview for " + item.itemURI, e);
        }
        return ProcessingContext.builder()
                .metadata(field, outURI.toString())
                .build();
    }

    private void generatePreview(URI inURI, URI outURI, ProcessingContext ctx) throws IOException {
        Float frameCount = getDuration(inputFactory.createFFprobe(inURI, ctx));
        int frameTarget = (int) frameCount.intValue() / 10;
        File prepFile = Files.createTempFile("ffmpeg_preview", ".mp4").toFile();
        try {
            prepare(inputFactory.createFFmpeg(inURI, ctx), UrlOutput.toPath(prepFile.toPath()), frameTarget);
            encode(UrlInput.fromPath(prepFile.toPath()), outputFactory.create(outURI, ctx));
        } finally {
            if (prepFile != null) {
                prepFile.delete();
            }
        }
    }

    private void prepare(BaseInput<?> in, BaseOutput<?> out, int frameTarget) throws IOException {
        FFmpeg ffmpeg = FFmpeg.atPath()
                .setLogLevel(LogLevel.QUIET)
                .setOverwriteOutput(true);

        Arrays.stream(additionalArgs).forEach(a -> ffmpeg.addArgument(a));

        out.addArguments("-vframes", "10")
                .addArgument("-an")
                .addArguments("-qscale:v", "1")
                .addArguments("-vf", buildFilter(frameTarget));
        out.setFormat("image2pipe")
                .addArguments("-vcodec", "ppm");

        ffmpeg.addInput(in)
                .addOutput(out);

        ffmpeg.execute();
    }

    private String buildFilter(int frameTarget) {
        return "fps=1/" + frameTarget
                + ", scale=iw*min(" + width + "/iw\\," + height + "/ih):ih*min(" + width + "/iw\\," + height + "/ih):flags=lanczos, pad=" + width + ":" + height + ":("
                + width + "-iw*min("
                + width + "/iw\\," + height + "/ih))/2:(" + height + "-ih*min(" + width + "/iw\\," + height + "/ih))/2, unsharp=5:5:0.5:5:5:0.5";
    }

    private void encode(BaseInput<?> in, BaseOutput<?> out) throws IOException {

        FFmpeg ffmpeg = FFmpeg.atPath()
                .setLogLevel(LogLevel.QUIET)
                .setOverwriteOutput(true);

        in.addArguments("-framerate", "1");

        out.setCodec(StreamType.VIDEO, "libx264");
        out.addArguments("-profile:v", "baseline");
        out.addArguments("-level", "3.0");
        out.addArguments("-tune", "stillimage");
        out.setFrameRate(30);
        out.setPixelFormat("yuv420p");

        ffmpeg.addInput(in).addOutput(out);

        ffmpeg.execute();
    }

    private Float getDuration(com.github.kokorin.jaffree.ffprobe.Input in) throws IOException {
        FFprobeResult result = FFprobe.atPath()
                .setShowStreams(true)
                .setLogLevel(LogLevel.QUIET)
                .setShowEntries("format=duration")
                .setShowPrivateData(false)
                .setShowStreams(false)
                .setInput(in)
                .execute();
        return result.getFormat().getDuration();
    }

    public static void main(String[] args) throws Exception {
        URLInputOutputFactory ioFactory = new URLInputOutputFactory();
        FfmpegVideoPreviewItemProcessor itemProcessor = new FfmpegVideoPreviewItemProcessor(ioFactory, ioFactory, null, 320, 240, "video-preview");
        URI in = new URI("file:///media/dell/storage/AquaTeen_O_Espirito_Cibernetico_do_Natal_Passado.avi");
        URI out = new URI("file:///media/dell/storage/AquaTeen_O_Espirito_Cibernetico_do_Natal_Passado_preview.mp4");
        itemProcessor.generatePreview(in, out, ProcessingContext.EMPTY);

    }

}
