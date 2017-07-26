package com.defano.jsegue;

import java.awt.image.BufferedImage;

public interface SegueAnimationObserver {
    void onFrameRendered(BufferedImage image);
}
