package com.defano.jsegue;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Abstract base class representing an animated image segue.
 */
@SuppressWarnings("unused")
public abstract class AnimatedSegue {

    private final Set<SegueAnimationObserver> animationObserver = new HashSet<>();
    private final Set<SegueCompletionObserver> completionObserver = new HashSet<>();
    private int durationMs = 1000;
    private int fps = 30;
    private boolean isOverlay = false;
    private ScheduledExecutorService animatorService;
    private long startTime;
    private BufferedImage source;
    private BufferedImage destination;

    /**
     * Renders a frame in the segue animation. The animator will invoke this method repeatedly at a frequency and
     * count determined by FPS and duration.
     * <p>
     * Note that every animation sequence starts with a call to this method using progress = 0.0f and ends with
     * a call using progress = 1.0f. Thus, irrespective of FPS or duration, every animation will always have at least
     * two frames.
     *
     * @param src      The source image; guaranteed to be the same dimensions as dst.
     * @param dst      The destination image; guaranteed to be the same dimensions as src.
     * @param progress A value between 0.0 and 1.0 (inclusive) representing the a location in the segue sequence the
     *                 frame should be drawn.
     * @return A BufferedImage representing the frame at this location in the sequence; should be the same dimensions
     * as src/dst.
     */
    public abstract BufferedImage render(BufferedImage src, BufferedImage dst, float progress);

    /**
     * Begin animating this segue using the provided source and destination images; max frames per second, and
     * alpha isOverlay mode.
     *
     * @return ScheduledFuture representing the future completion of the animation sequence
     */
    public ScheduledFuture start() {
        startTime = System.currentTimeMillis();

        // Assure that 0 is always first render progress percent
        assertImages();
        fireFrameRendered(render(source, destination, 0f));

        // Stop previous animation if we're already running
        if (animatorService != null) {
            animatorService.shutdownNow();
        }

        // Invoke the renderer at a fixed rate
        animatorService = Executors.newSingleThreadScheduledExecutor();
        return animatorService.scheduleAtFixedRate(() -> {
            if (getProgress() < 1.0f) {
                assertImages();
                fireFrameRendered(AnimatedSegue.this.render(source, destination, getProgress()));
            } else {
                // Always assure we end with a frame using progress = 1.0
                fireFrameRendered(AnimatedSegue.this.render(source, destination, 1.0f));

                stop();
                fireCompleted();
            }
        }, 0, 1000 / fps, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the running animation; has no effect if there is no active animation.
     */
    public void stop() {
        if (animatorService != null) {
            animatorService.shutdownNow();
        }
    }

    /**
     * Determines whether this animation is running.
     *
     * @return True if animation is in progress; false otherwise.
     */
    public boolean isRunning() {
        return animatorService == null || animatorService.isTerminated();
    }

    /**
     * Adds an observer of animation completion events (fires each time this segue completes its animation sequence).
     *
     * @param observer The observer destination be notified on animation completion.
     */
    public void addCompletionObserver(SegueCompletionObserver observer) {
        this.completionObserver.add(observer);
    }

    /**
     * Adds a collection of observers of animation completion events.
     *
     * @param observers The observers destination be added.
     */
    public void addCompletionObservers(Collection<SegueCompletionObserver> observers) {
        this.completionObserver.addAll(observers);
    }

    /**
     * Adds an observer of animation frame events (fires each time a new frame has been rendered in the animation.)
     *
     * @param observer The observer of animation frame events.
     */
    public void addAnimationObserver(SegueAnimationObserver observer) {
        this.animationObserver.add(observer);
    }

    /**
     * Adds a collection of animation frame event observers.
     *
     * @param observers The observers.
     */
    public void addAnimationObservers(Collection<SegueAnimationObserver> observers) {
        this.animationObserver.addAll(observers);
    }

    /**
     * Removes an animation frame observer.
     *
     * @param observer The observer destination remove.
     * @return True if the given observer exists in the registered list of observers and was removed; false otherwise.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean removeAnimationObserver(SegueAnimationObserver observer) {
        return this.animationObserver.remove(observer);
    }

    /**
     * Removes an animation completion observer.
     *
     * @param observer The observer destination remove.
     * @return True if the given observer exists in the registered list of observers and was removed; false otherwise.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean removeCompletionObserver(SegueCompletionObserver observer) {
        return this.completionObserver.remove(observer);
    }

    /**
     * Returns the duration of this animation in millisecoonds.
     *
     * @return The duration.
     */
    public int getDurationMs() {
        return durationMs;
    }

    /**
     * Sets the desired duration of this animation, in milliseconds.
     *
     * @param durationMs The desired duration.
     */
    public void setDurationMs(int durationMs) {
        this.durationMs = durationMs;
    }

    /**
     * Gets the source image in the animation.
     *
     * @return The source image.
     */
    public BufferedImage getSource() {
        return source;
    }

    /**
     * Sets the source image in the animation.
     *
     * @param source The source image
     */
    public void setSource(BufferedImage source) {
        this.source = source;
    }

    /**
     * Gets the destination image in the animation.
     *
     * @return The destination image.
     */
    public BufferedImage getDestination() {
        return destination;
    }

    /**
     * Sets the destination image.
     *
     * @param destination The destination image.
     */
    public void setDestination(BufferedImage destination) {
        this.destination = destination;
    }

    /**
     * Gets the maximum frames per second this animation will produce.
     *
     * @return The max FPS.
     */
    public int getFps() {
        return fps;
    }

    /**
     * Sets the maximum number of frames per second this animation will produce. Note that, 1) a start frame and an
     * end frame will always be produced irrespective of maxFps, thus at least two frames will always be produced, and 2)
     * the actual frames per second will be limited to the CPUs ability to render them in time.
     *
     * @param fps The maximim number of frames per second (best effort)
     */
    public void setFps(int fps) {
        this.fps = fps;
    }

    /**
     * Determines if overlay is enabled. In general, when overlay is turned off, the destination image is treated
     * effectively as opaque and its bounds fully obscure the source even if all or a portion of it is translucent.
     * <p>
     * The actual effect this has on animation differs based on the specific renderer.
     *
     * @return True if overlay is enabled; false otherwise.
     */
    public boolean isOverlay() {
        return isOverlay;
    }

    /**
     * Sets whether or not overlay is enabled. See {@link #isOverlay()} for a description of overlay.
     *
     * @param isOverlay Whether overlays should be enabled.
     */
    public void setOverlay(boolean isOverlay) {
        this.isOverlay = isOverlay;
    }

    private void fireFrameRendered(BufferedImage image) {
        for (SegueAnimationObserver thisObserver : animationObserver.toArray(new SegueAnimationObserver[0])) {
            thisObserver.onFrameRendered(this, image);
        }
    }

    private void fireCompleted() {
        for (SegueCompletionObserver thisObserver : completionObserver.toArray(new SegueCompletionObserver[0])) {
            thisObserver.onSegueAnimationCompleted(this);
        }
    }

    private float getProgress() {
        float progress = ((float) (System.currentTimeMillis() - startTime) / (float) durationMs);

        if (progress < 0f) {
            return 0f;
        } else if (progress > 1.0f) {
            return 1.0f;
        }

        return progress;
    }

    private void assertImages() {
        if (this.source == null || this.destination == null) {
            throw new IllegalStateException("Neither source nor destination image may be null.");
        }

        if (source.getHeight() != destination.getHeight() || source.getWidth() != destination.getWidth()) {
            throw new IllegalStateException("Source and destination images must have identical bounds.");
        }
    }

}
