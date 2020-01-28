package com.defano.jsegue;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A builder of animated image segues.
 */
@SuppressWarnings("unused")
public class SegueBuilder {

	public static final Class<? extends AnimatedSegue> DISSOLVE = null;
	private Class<? extends AnimatedSegue> segue;
	private BufferedImage source;
	private BufferedImage destination;
	private Paint sourcePaint;
	private Paint destinationPaint;
	private int maxFps = 30;
	private int durationMs = 1500;
	private boolean overlay = false;
	private Set<SegueAnimationObserver> animationObservers = new HashSet<>();
	private Set<SegueCompletionObserver> completionObservers = new HashSet<>();

	private SegueBuilder(Class<? extends AnimatedSegue> segue) {
		this.segue = segue;
	}

	/**
	 * Create a segue builder for the specified segue name.
	 *
	 * @param seque The class of the effect to build.
	 * @return The builder for this segue
	 */
	public static SegueBuilder of(Class<? extends AnimatedSegue> seque) {
		return new SegueBuilder(seque);
	}

	/**
	 * Set the source image for this segue.
	 * <p>
	 * If the dimensions of this image do no match that of the destination, the
	 * image may be resized to match the larger height/width.
	 *
	 * @param src The source image
	 * @return This builder object
	 */
	public SegueBuilder withSource(BufferedImage src) {
		this.source = src;
		return this;
	}

	/**
	 * Set the destination image for this segue.
	 * <p>
	 * If the dimensions of this image do no match that of the destination, the
	 * image may be resized to match the larger height/width.
	 *
	 * @param dst The destination image
	 * @return This builder object
	 */
	public SegueBuilder withDestination(BufferedImage dst) {
		this.destination = dst;
		return this;
	}

	/**
	 * Sets a "paint" source image. May be a solid color, or any Paint subclass
	 * including textured paint or gradients. Note that only the source or the
	 * destination may be a paint; both cannot be paint.
	 *
	 * @param paint The paint to use as the source image.
	 * @return This builder object
	 */
	public SegueBuilder withSource(Paint paint) {
		this.sourcePaint = paint;
		return this;
	}

	/**
	 * Sets a "paint" destination image. May be a solid color, or any Paint subclass
	 * including textured paint or gradients. Note that only the source or the
	 * destination may be a paint; both cannot be paint.
	 *
	 * @param paint The paint to use as the destination image
	 * @return This builder object
	 */
	public SegueBuilder withDestination(Paint paint) {
		this.destinationPaint = paint;
		return this;
	}

	/**
	 * Sets the maximum number of frames that will be rendered in each second. Note
	 * that this specifies only a cap; during real-time animation execution the
	 * system may not achieve this rate. Also note that a minimum of three frames
	 * are always guaranteed to be created irrespective of this value or duration.
	 * <p>
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
	 * <p>
	 * See{@link AnimatedSegue#setDurationMs(int)}
	 *
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
	 * @param unit     The units of time that duration is measured in
	 * @return This builder object
	 */
	public SegueBuilder withDuration(int duration, TimeUnit unit) {
		this.durationMs = (int) unit.toMillis(duration);
		return this;
	}

	/**
	 * Enables or disables alpha overlay mode in the renderers.
	 * <p>
	 * See {@link AnimatedSegue#setOverlay(boolean)}.
	 *
	 * @param overlayDestination True to enable overlay; false to disable.
	 * @return This builder object.
	 */
	public SegueBuilder overlay(boolean overlayDestination) {
		this.overlay = overlayDestination;
		return this;
	}

	/**
	 * Adds an observer of animation frame generation (invoked each time a new frame
	 * is rendered).
	 *
	 * @param observer The observer
	 * @return This builder object
	 */
	public SegueBuilder withAnimationObserver(SegueAnimationObserver observer) {
		this.animationObservers.add(observer);
		return this;
	}

	/**
	 * Adds an observer of animation completion (invoked when animation is finished)
	 *
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
	 * Note these limitations: - A source and destination must be provided. - Either
	 * the source or the destination must be a BufferedImage (cant use two Paints) -
	 * If the images are not the same size, they will be resized to the larger
	 * dimension.
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
		BufferedImage theSource = source == null
				? paintImage(destination.getWidth(), destination.getHeight(), sourcePaint)
				: source;
		BufferedImage theDestination = destination == null
				? paintImage(source.getWidth(), source.getHeight(), destinationPaint)
				: destination;

		// Resize to largest dimensions of not equal
		if (theSource.getWidth() != theDestination.getWidth() || theSource.getHeight() != theDestination.getHeight()) {
			int targetWidth = Math.max(theSource.getWidth(), theDestination.getWidth());
			int targetHeight = Math.max(theDestination.getWidth(), theDestination.getHeight());

			theSource = enlargeImage(targetWidth, targetHeight, theSource);
			theDestination = enlargeImage(targetWidth, targetHeight, theDestination);
		}

		try {
			AnimatedSegue effect = segue.newInstance();
			effect.setSource(theSource);
			effect.setDestination(theDestination);
			effect.setDurationMs(durationMs);
			effect.setFps(maxFps);
			effect.setOverlay(overlay);
			effect.addAnimationObservers(animationObservers);
			effect.addCompletionObservers(completionObservers);

			return effect;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(
					"Failed to instantiate segue. Verify that it has a public, no-arg constructor.", e);
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
