package com.defano.jsegue;

import com.defano.jsegue.renderers.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A builder of animated image segues.
 */
public class SegueBuilder {

    private final SegueName name;
    private BufferedImage source;
    private BufferedImage destination;
    private Paint sourcePaint;
    private Paint destinationPaint;
    private int maxFps = 30;
    private int durationMs = 1500;
    private boolean overlay = false;
    private Set<SegueAnimationObserver> animationObservers = new HashSet<>();
    private Set<SegueCompletionObserver> completionObservers = new HashSet<>();

    private SegueBuilder(SegueName name) {
        this.name = name;
    }

    /**
     * Create a segue builder for the specified segue name.
     * @param name The name of the effect to build.
     * @return The builder for this segue
     */
    public static SegueBuilder of (SegueName name) {
        return new SegueBuilder(name);
    }

    /**
     * Set the source image for this segue.
     *
     * If the dimensions of this image do no match that of the destination, the image may be resized to match
     * the larger height/width.
     * @param src The source image
     * @return This builder object
     */
    public SegueBuilder withSource (BufferedImage src) {
        this.source = src;
        return this;
    }

    /**
     * Set the destination image for this segue.
     *
     * If the dimensions of this image do no match that of the destination, the image may be resized to match
     * the larger height/width.
     * @param dst The destination image
     * @return This builder object
     */
    public SegueBuilder withDestination(BufferedImage dst) {
        this.destination = dst;
        return this;
    }

    /**
     * Sets a "paint" source image. May be a solid color, or any Paint subclass including textured paint or gradients.
     * Note that only the source or the destination may be a paint; both cannot be paint.
     *
     * @param paint The paint to use as the source image.
     * @return This builder object
     */
    public SegueBuilder withSource(Paint paint) {
        this.sourcePaint = paint;
        return this;
    }

    /**
     * Sets a "paint" destination image. May be a solid color, or any Paint subclass including textured paint or gradients.
     * Note that only the source or the destination may be a paint; both cannot be paint.
     *
     * @param paint The paint to use as the destination image
     * @return This builder object
     */
    public SegueBuilder withDestination(Paint paint) {
        this.destinationPaint = paint;
        return this;
    }

    /**
     * Sets the maximum number of frames that will be rendered in each second. Note that this specifies only a cap;
     * during real-time animation execution the system may not achieve this rate. Also note that a minimum of three
     * frames are always guaranteed to be created irrespective of this value or duration.
     *
     * See {@link AnimatedSegue#setFps(int)}
     *
     * @param maxFps Maximum number of frames per second
     * @return This builder object
     */
    public SegueBuilder withMaxFramesPerSecond(int maxFps) {
        this.maxFps = maxFps;
        return this;
    }

    /**
     * Sets the duration of this animation, in milliseconds.
     *
     * See{@link AnimatedSegue#setDurationMs(int)}
     * @param durationMs The duration of the animation, in milliseconds.
     * @return This builder object
     */
    public SegueBuilder withDurationMs(int durationMs) {
        this.durationMs = durationMs;
        return this;
    }

    /**
     * Sets the duration of this animation, in user-provided units.
     *
     * @param duration The duration of this animation
     * @param unit The units of time that duration is measured in
     * @return This builder object
     */
    public SegueBuilder withDuration(int duration, TimeUnit unit) {
        this.durationMs = (int) unit.toMillis(duration);
        return this;
    }

    /**
     * Enables or disables alpha overlay mode in the renderers.
     *
     * See {@link AnimatedSegue#setOverlay(boolean)}.
     * @param overlayDestination True to enable overlay; false to disable.
     * @return This builder object.
     */
    public SegueBuilder overlay(boolean overlayDestination) {
        this.overlay = overlayDestination;
        return this;
    }

    /**
     * Adds an observer of animation frame generation (invoked each time a new frame is rendered).
     * @param observer The observer
     * @return This builder object
     */
    public SegueBuilder withAnimationObserver(SegueAnimationObserver observer) {
        this.animationObservers.add(observer);
        return this;
    }

    /**
     * Adds an observer of animation completion (invoked when animation is finished)
     * @param observer The observer
     * @return This builder object
     */
    public SegueBuilder withCompletionObserver(SegueCompletionObserver observer) {
        this.completionObservers.add(observer);
        return this;
    }

    /**
     * Builds an animated segue based on the values provided to the builder.
     * <p>
     * Note these limitations:
     * - A source and destination must be provided.
     * - Either the source or the destination must be a BufferedImage (cant use two Paints)
     * - If the images are not the same size, they will be resized to the larger dimension.
     *
     * @return The animated segue.
     */
    public AnimatedSegue build() {
        if (sourcePaint == null && source == null) {
            throw new IllegalArgumentException("Must specify a source before building.");
        }

        if (destinationPaint == null && destination == null) {
            throw new IllegalArgumentException("Must specify a destination before building.");
        }

        if (source == null && destination == null) {
            throw new IllegalArgumentException("Cannot use paint for both source and destination.");
        }

        // Create paint images as needed
        BufferedImage theSource = source == null ? paintImage(destination.getWidth(), destination.getHeight(), sourcePaint) : source;
        BufferedImage theDestination = destination == null ? paintImage(source.getWidth(), source.getHeight(), destinationPaint) : destination;

        // Resize to largest dimensions of not equal
        if (theSource.getWidth() != theDestination.getWidth() || theSource.getHeight() != theDestination.getHeight()) {
            int targetWidth = Math.max(theSource.getWidth(), theDestination.getWidth());
            int targetHeight = Math.max(theDestination.getWidth(), theDestination.getHeight());

            theSource = enlargeImage(targetWidth, targetHeight, theSource);
            theDestination = enlargeImage(targetWidth, targetHeight, theDestination);
        }

        AnimatedSegue effect = getEffect(name);
        effect.setSource(theSource);
        effect.setDestination(theDestination);
        effect.setDurationMs(durationMs);
        effect.setFps(maxFps);
        effect.setOverlay(overlay);
        effect.addAnimationObservers(animationObservers);
        effect.addCompletionObservers(completionObservers);

        return effect;
    }

    private AnimatedSegue getEffect(SegueName name) {
        switch (name) {
            case DISSOLVE:
                return new DissolveEffect();
            case SCROLL_LEFT:
                return new ScrollLeftEffect();
            case SCROLL_RIGHT:
                return new ScrollRightEffect();
            case SCROLL_UP:
                return new ScrollUpEffect();
            case SCROLL_DOWN:
                return new ScrollDownEffect();
            case BARN_DOOR_OPEN:
                return new BarnDoorOpenEffect();
            case BARN_DOOR_CLOSE:
                return new BarnDoorCloseEffect();
            case WIPE_LEFT:
                return new WipeLeftEffect();
            case WIPE_RIGHT:
                return new WipeRightEffect();
            case WIPE_UP:
                return new WipeUpEffect();
            case WIPE_DOWN:
                return new WipeDownEffect();
            case IRIS_OPEN:
                return new IrisOpenEffect();
            case IRIS_CLOSE:
                return new IrisCloseEffect();
            case ZOOM_IN:
                return new ZoomInEffect();
            case ZOOM_OUT:
                return new ZoomOutEffect();
            case PLAIN:
                return new PlainEffect();
            case STRETCH_FROM_TOP:
                return new StretchFromTopEffect();
            case STRETCH_FROM_BOTTOM:
                return new StretchFromBottomEffect();
            case STRETCH_FROM_CENTER:
                return new StretchFromCenterEffect();
            case SHRINK_TO_BOTTOM:
                return new ShrinkToBottomEffect();
            case SHRINK_TO_TOP:
                return new ShrinkToTopEffect();
            case SHRINK_TO_CENTER:
                return new ShrinkToCenterEffect();
            case VENETIAN_BLINDS:
                return new BlindsEffect();
            case CHECKERBOARD:
                return new CheckerboardEffect();

            default:
                throw new IllegalArgumentException("Bug! Unhandled visual effect: " + name);
        }
    }

    private BufferedImage enlargeImage(int width, int height, BufferedImage image) {
        BufferedImage enlarged = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = enlarged.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return enlarged;
    }

    private BufferedImage paintImage(int width, int height, Paint paint) {
        BufferedImage enlarged = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = enlarged.createGraphics();
        g.setPaint(paint);
        g.fillRect(0, 0, width, height);
        g.dispose();

        return enlarged;
    }
}
