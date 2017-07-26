package com.defano.jsegue.renderers;

import com.defano.jsegue.AnimatedSegue;

import java.awt.image.BufferedImage;

public class FreezeEffect extends AnimatedSegue {

    @Override
    public BufferedImage render(BufferedImage src, BufferedImage dst, float progress) {

        // No-op effect that simply renders the original image
        return src;
    }
}
