package com.defano.jsegue.renderers;

/**
 * The source image collapses over the destination in a rectangle aperture.
 */
public class ZoomInEffect extends AbstractZoomEffect {

    public ZoomInEffect() {
        super(ZoomShape.RECTANGLE, ZoomDirection.ZOOM_IN);
    }
}
