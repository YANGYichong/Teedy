package com.sismics.util;

import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * Test of the image utilities.
 * 
 * @author bgamard
 */
public class TestImageUtil {

    @Test
    public void computeGravatarTest() {
        Assert.assertEquals("0bc83cb571cd1c50ba6f3e8a78ef1346", ImageUtil.computeGravatar("MyEmailAddress@example.com "));
    }

    @Test
    public void computeGravatarNullTest() {
        Assert.assertNull(ImageUtil.computeGravatar(null));
    }

    @Test
    public void isBlackTest() {
        BufferedImage binaryImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
        binaryImage.getRaster().setSample(0, 0, 0, 0);
        Assert.assertTrue(ImageUtil.isBlack(binaryImage, 0, 0));
        binaryImage.getRaster().setSample(0, 0, 0, 1);
        Assert.assertFalse(ImageUtil.isBlack(binaryImage, 0, 0));

        BufferedImage rgbImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        rgbImage.setRGB(0, 0, Color.BLACK.getRGB());
        Assert.assertTrue(ImageUtil.isBlack(rgbImage, 0, 0));
        rgbImage.setRGB(0, 0, Color.WHITE.getRGB());
        Assert.assertFalse(ImageUtil.isBlack(rgbImage, 0, 0));
        Assert.assertFalse(ImageUtil.isBlack(rgbImage, -1, 0));
    }

    @Test
    public void writeJpegTest() throws Exception {
        BufferedImage alphaImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        alphaImage.setRGB(0, 0, new Color(255, 0, 0, 128).getRGB());
        ByteArrayOutputStream alphaOutput = new ByteArrayOutputStream();
        ImageUtil.writeJpeg(alphaImage, alphaOutput);
        Assert.assertTrue(alphaOutput.size() > 0);

        BufferedImage rgbImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        rgbImage.setRGB(0, 0, Color.BLUE.getRGB());
        ByteArrayOutputStream rgbOutput = new ByteArrayOutputStream();
        ImageUtil.writeJpeg(rgbImage, rgbOutput);
        Assert.assertTrue(rgbOutput.size() > 0);
    }
}
