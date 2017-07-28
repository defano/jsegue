package com.defano.jsegue;

/**
 * An observer of completing animations.
 */
public interface SegueCompletionObserver {
    /**
     * Fired to indicate that an animation has rendered its last frame and is now complete.
     * @param segue The segue animation which completed.
     */
    void onSegueAnimationCompleted(AnimatedSegue segue);
}
