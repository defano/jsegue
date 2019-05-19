package com.defano.jsegue.renderers;

import com.defano.jsegue.AnimatedSegue;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Cross-dissolve from source to destination by randomly replacing source pixels with destination pixels.
 */
@SuppressWarnings("unused")
public class PixelDissolveEffect extends AnimatedSegue {

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {

        // Random function must use a consistent seed at each render, otherwise pixels will "un-dissolve" adn appear to
        // flicker
        Random random = new Random(0);

        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                int rgb = random.nextInt(101) <= Math.max(1, (int) (100.0 * progress)) ? dst.getRGB(x, y) : src.getRGB(x, y);
                frame.setRGB(x, y, rgb);
            }
        }

        return frame;
    }
}
