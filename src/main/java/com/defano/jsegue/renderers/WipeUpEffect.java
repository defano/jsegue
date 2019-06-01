package com.defano.jsegue.renderers;

import com.defano.jsegue.AnimatedSegue;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Slides the destination image over the source from bottom to top.
 */
public class WipeUpEffect extends AnimatedSegue {

    /** {@inheritDoc} */
    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {
        BufferedImage frame = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();

        // Calculate wipe distance
        int distance = Math.min(src.getHeight() - 1, (int) (progress * src.getHeight()));
        BufferedImage sub = src.getSubimage(0, 0, src.getWidth(), src.getHeight() - distance);

        g.drawImage(dst, 0, 0, null);
        g.drawImage(sub, 0, 0, null);

        return frame;
    }
}
