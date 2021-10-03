package com.github.euler.dl4j;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.datavec.image.loader.Java2DNativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;

public class JavaRGBImageDataLoader implements DataLoader {

    private final int width;
    private final int height;
    private final InterpolationType interpolationType;

    private final Java2DNativeImageLoader loader = new Java2DNativeImageLoader();

    public JavaRGBImageDataLoader(int width, int height, InterpolationType interpolationType) {
        super();
        this.width = width;
        this.height = height;
        this.interpolationType = interpolationType;
    }

    public JavaRGBImageDataLoader(InterpolationType interpolationType) {
        this(-1, -1, interpolationType);
    }

    @Override
    public INDArray load(InputStream in) throws IOException {
        BufferedImage bim = ImageIO.read(in);
        bim = toRGB(bim);
        if (this.width >= 0 || this.height >= 0) {
            bim = scale(bim, this.width, this.height);
        }
        return loader.asMatrix(bim, false);
    }

    private BufferedImage toRGB(BufferedImage bim) {
        int width = bim.getWidth();
        int height = bim.getHeight();
        BufferedImage rgb = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        rgb.createGraphics().drawImage(bim, 0, 0, width, height, null);
        return rgb;
    }

    public BufferedImage scale(BufferedImage bim, int width, int height) {
        if (width <= 0) {
            width = bim.getWidth();
        }
        if (height <= 0) {
            height = bim.getHeight();
        }
        double sx = (double) width / (double) bim.getWidth();
        double sy = (double) height / (double) bim.getHeight();
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(sx, sy);
        AffineTransformOp op = new AffineTransformOp(scaleTransform, this.interpolationType.type);
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        scaled = op.filter(bim, scaled);
        return scaled;
    }

    public static enum InterpolationType {

        NEAREST(AffineTransformOp.TYPE_NEAREST_NEIGHBOR), BILINEAR(AffineTransformOp.TYPE_BILINEAR), BICUBIC(AffineTransformOp.TYPE_BICUBIC);

        private final int type;

        private InterpolationType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

    }

}
