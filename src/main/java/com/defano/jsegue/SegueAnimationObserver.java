package com.defano.jsegue;

import java.awt.image.BufferedImage;

/**
 * An observer of new frames produced by a segue animation.
 */
public interface SegueAnimationObserver {

    /**
     * Fired when a new frame of a segue animation has been generated. This method will be invoked once for each frame
     * produced in an animation; the exact number of times this method will be invoked depends on the animations
     * maxFps and duration properties.
     *
     * @param segue The animation which produced the frame
     * @param image The frame produced by the animation.
     */
    void onFrameRendered(AnimatedSegue segue, BufferedImage image);
}
