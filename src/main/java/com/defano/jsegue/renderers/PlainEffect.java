package com.defano.jsegue.renderers;

import com.defano.jsegue.AnimatedSegue;

import java.awt.image.BufferedImage;

/**
 * A no-op transition that simply renders the source image.
 */
public class PlainEffect extends AnimatedSegue {

    /** {@inheritDoc} */
    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {

        // No-op effect that simply renders the original image
        return src;
    }
}
