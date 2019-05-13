package com.defano.jsegue.renderers;

import com.defano.jsegue.AnimatedSegue;

import java.awt.image.BufferedImage;
import java.util.Random;

public class PixelDissolveEffect extends AnimatedSegue {

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {

        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int percent = Math.max(1, (int) (100.0 * progress));
        Random random = new Random();

        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                int rgb = (random.nextInt(101) <= percent) ? dst.getRGB(x, y) : src.getRGB(x, y);
                frame.setRGB(x, y, rgb);
            }
        }

        return frame;
    }
}
